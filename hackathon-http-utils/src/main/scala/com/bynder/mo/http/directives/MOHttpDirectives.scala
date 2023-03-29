package com.bynder.mo.http.directives

import akka.http.scaladsl.model.{HttpHeader, StatusCodes}
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.{Directive1, RequestContext}
import com.bynder.mo.AWSSecretLoader
import com.bynder.mo.http.utils.{JWTParser, JWTPayload}

import java.util.UUID
import scala.util.{Failure, Success, Try}

object MOHttpDirectives:
  def validateUUID(id: String): Directive1[UUID]                                 =
    Try(UUID.fromString(id)) match
      case Failure(_)        => complete(StatusCodes.BadRequest, "provided id is invalid UUID")
      case Success(objectId) => provide(objectId)

  def authorizeJWT(headerName: String = "Authorization"): Directive1[JWTPayload] =
    headerValueByName(headerName).flatMap { jwt =>
      Try(JWTParser.parseJWT(jwt)) match
        case Failure(e)           => complete(StatusCodes.BadRequest, s"${e.getLocalizedMessage}")
        case Success(accountInfo) =>
          provide(accountInfo)
    }

  def validateJWT(ctx: RequestContext): Directive1[JWTPayload] =
    val headers     = ctx.request.headers
    val authHeader  = "authorization"
    val headerNames = headers.map(_.name.toLowerCase)

    parameters("account_id".optional).flatMap { (accountIdParameter: Option[String]) =>
      if headerNames.contains(authHeader) then authorizeJWT().map(x => x)
      else
        accountIdParameter match
          case Some(accountId) =>
            Try(UUID.fromString(convertAccountIdToUUID(accountId))) match
              case Failure(_)             => complete(StatusCodes.BadRequest, "account_id is invalid UUID")
              case Success(accountIdUuid) =>
                val payload = JWTPayload(accountIdUuid, UUID(0, 0).toString)
                provide(payload)
          case _               =>
            complete(StatusCodes.BadRequest, "Request needs to provide Authorization or account_id query parameter")
    }

  def convertAccountIdToUUID(input: String): String =
    val lastDash = input.charAt(23)
    if lastDash != '-' then input.patch(23, "-", 0)
    else input
