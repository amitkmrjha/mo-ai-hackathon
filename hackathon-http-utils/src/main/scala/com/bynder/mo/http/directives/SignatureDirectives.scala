package com.bynder.mo.http.directives

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.RouteResult.Complete
import com.bynder.mo.AWSSecretLoader
import com.typesafe.config.ConfigFactory
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.crypto.Signer
import org.bouncycastle.crypto.params.{AsymmetricKeyParameter, Ed25519PrivateKeyParameters, Ed25519PublicKeyParameters}
import org.bouncycastle.crypto.signers.Ed25519Signer
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemReader
import spray.json.*
import spray.json.DefaultJsonProtocol.StringJsonFormat

import java.io.{ByteArrayInputStream, InputStreamReader, StringReader}
import java.net.{URLDecoder, URLEncoder}
import java.security.*
import java.security.spec.{PKCS8EncodedKeySpec, X509EncodedKeySpec}
import java.util.Base64
import scala.StringContext.processEscapes
import scala.collection.mutable.Map
import scala.util.{Failure, Success, Try}

object SignatureDirectives:
  val config      = ConfigFactory.load()
  val secretId    = config.getString("boss.fileservices.secretId").toString
  val settingName = config.getString("boss.fileservices.settingName").toString
  val region      = config.getString("boss.region").toString
  val secret      = "validation_key"

  // Validation key scheme is version: (algorithm, key)
  val validationKeys: scala.collection.mutable.Map[String, (String, PublicKey)] = Map()

  def addValidationSecret(publicKey: PublicKey, version: String, algorithm: String) =
    validationKeys += (version -> (algorithm, publicKey))

  def validateSignature(origianlPath: String, signature: String, keyVersion: String): Directive1[Boolean] =
    validationKeys.get(keyVersion) match
      case Some((algorithm: String, publicKey: PublicKey)) =>
        verify(signature, algorithm, keyVersion, origianlPath) match
          case Success(value) => provide(value)
          case Failure(e)     => provide(false)
      case _                                               =>
        getValidationSecret(secretId, settingName) match
          case Success(s: String) =>
            getSecret(s, keyVersion) match
              case Some(secret) =>
                val reader = new PemReader(new StringReader(processEscapes(secret.convertTo[String])))
                val spec   = new X509EncodedKeySpec(reader.readPemObject.getContent)
                val oid    = SubjectPublicKeyInfo
                  .getInstance(spec.getEncoded)
                  .getAlgorithm
                  .getAlgorithm
                  .toString

                Try(KeyFactory.getInstance(oid, new BouncyCastleProvider).generatePublic(spec)) match
                  case Success(key) =>
                    val algorithm = key.getAlgorithm
                    addValidationSecret(key, keyVersion, algorithm)
                    verify(signature, algorithm, keyVersion, origianlPath) match
                      case Success(value) => provide(value)
                      case Failure(e)     => provide(false)
                  case Failure(_)   =>
                    complete(StatusCodes.BadRequest, "Invalid version")
              case _            =>
                complete(StatusCodes.BadRequest, "Invalid version")
          case Failure(_)         =>
            complete(StatusCodes.BadRequest, "Invalid version")

  def verify(signature: String, algorithm: String, keyVersion: String, originalPath: String): Try[Boolean] =
    val algo          = algorithm match
      case "EC"      => "SHA1withECDSA"
      case "Ed25519" => "Ed25519"
      case _         => throw new RuntimeException("Invalid Version")

    val validationKey = validationKeys.get(keyVersion) match
      case Some(_, pKey) => pKey
      case None          => throw new RuntimeException("Invalid Version")

    Try {
      val sig    = URLDecoder.decode(signature, "UTF-8")
      val verify = Signature.getInstance(algo)
      verify.initVerify(validationKeys.get(keyVersion).get._2)
      verify.update(originalPath.getBytes("UTF-8"))
      verify.verify(Base64.getDecoder.decode(signature))
    }

  def getValidationSecret(secretId: String, settingName: String): Try[String] =
    val awsSecretLoader = new AWSSecretLoader(region)
    awsSecretLoader.getSecretValue(settingName)

  def getSecret(v: String, version: String): Option[JsValue] =
    val key = s"${secret}_${version}"
    v.parseJson.asJsObject.fields.get(key)
