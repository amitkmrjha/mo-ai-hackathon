package com.bynder.mo

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.{
  GetSecretValueRequest,
  InvalidParameterException,
  InvalidRequestException,
  ResourceNotFoundException
}
import spray.json.DefaultJsonProtocol.*
import spray.json.JsonParser

import scala.util.Try

class AWSSecretLoader(reg: String):
  val region = Region.of(reg)
  val client = SecretsManagerClient
    .builder()
    .region(region)
    .credentialsProvider(DefaultCredentialsProvider.create)
    .build()

  def getSecretValue(settingName: String): Try[String] =
    val getSecretValueRequest = GetSecretValueRequest
      .builder()
      .secretId(settingName)
      .build()

    Try(client.getSecretValue(getSecretValueRequest).secretString())
