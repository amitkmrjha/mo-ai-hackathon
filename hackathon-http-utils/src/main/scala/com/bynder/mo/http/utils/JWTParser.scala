package com.bynder.mo.http.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol.*
import spray.json.{
  DefaultJsonProtocol,
  DeserializationException,
  JsNull,
  JsString,
  JsValue,
  JsonFormat,
  RootJsonFormat,
  enrichAny,
  enrichString
}

import java.util.{Base64, UUID}
import scala.util.{Failure, Success, Try}

object JWTParser extends MOJsonFormat:

  val accountIdFieldName = "account_id"
  val userIdFieldName    = "user_id"

  given JWTPayloadFormat: RootJsonFormat[JWTPayload] with
    override def read(json: JsValue): JWTPayload     =
      json.asJsObject.getFields(accountIdFieldName, userIdFieldName) match
        case Seq(JsString(accountId), JsString(userId)) =>
          val accountIdUuid = Try(UUID.fromString(accountId))
            .getOrElse(throw new DeserializationException("Account Id UUID is malformed. Cannot parse"))
          JWTPayload(accountIdUuid, userId)
        case Seq(JsString(accountId))                   =>
          val accountIdUuid = Try(UUID.fromString(accountId))
            .getOrElse(throw new DeserializationException("Account Id UUID is malformed. Cannot parse"))
          JWTPayload(accountIdUuid, UUID(0, 0).toString)
        case _                                          => throw new DeserializationException("Missing Account Id")

    // Note: If we need to write JWT payload, map
    // accountId => account_id and userId => user_id
    override def write(payload: JWTPayload): JsValue = payload.toJson

  def parseJWT(authHeader: String): JWTPayload =
    val token_items = authHeader.split('.')
    Try(token_items(1)) match
      case Failure(e)       => throw new DeserializationException("JWT format incorrect")
      case Success(payload) =>
        val decoded_payload: String = new String(Base64.getDecoder().decode(payload))
        val accountInfo: JWTPayload = decoded_payload.parseJson.convertTo[JWTPayload]
        return accountInfo
