package com.bynder.mo.http.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCode
import com.bynder.mo.http.api.model.{HealthResponse, ServicesHealthResponse}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsNumber, JsString, JsValue, RootJsonFormat}

import java.time.Instant
import java.util.UUID
import scala.util.Try

trait MOJsonFormat extends SprayJsonSupport with DefaultJsonProtocol:
  given TimestampFormat: RootJsonFormat[Instant] with
    override def read(json: JsValue): Instant = json match
      case JsString(value) =>
        Instant.parse(value)
      case _               => throw new DeserializationException("Expected timestamp")
    override def write(obj: Instant): JsValue = JsString(obj.toString)

  given StatusCodesFormat: RootJsonFormat[StatusCode] with
    override def read(json: JsValue): StatusCode  = json match
      case JsNumber(value) =>
        value.intValue
      case _               => throw new DeserializationException("Expected statusCode")
    override def write(obj: StatusCode): JsNumber = JsNumber(obj.intValue)

  given UUIDFormat: RootJsonFormat[UUID] with
    override def read(json: JsValue): UUID  = json match
      case JsString(value) =>
        Try(UUID.fromString(value)).getOrElse(throw new DeserializationException("UUID is malformed. Cannot parse"))
      case _               => throw new DeserializationException("Expected UUID")
    override def write(obj: UUID): JsString = JsString(obj.toString)

  given ServicesHealthFormat: RootJsonFormat[ServicesHealthResponse] = jsonFormat2(ServicesHealthResponse.apply)

  given HealthFormat: RootJsonFormat[HealthResponse] = jsonFormat4(HealthResponse.apply)
