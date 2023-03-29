package com.bynder.mo.http.utils

import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.model.{HttpRequest, Uri}
import com.bynder.mo.http.client.ServerToken

import java.util.UUID
import scala.util.{Failure, Success, Try}

extension (request: HttpRequest) {
  def withToken(using server: ServerToken): HttpRequest = {
    request.withHeaders(List(Authorization(OAuth2BearerToken(server.token))))
  }
}

extension (uri: akka.http.scaladsl.model.Uri.type) {
  def forPath(using server: ServerToken)(path: String, query: Option[String]): Uri = {
    server.port match {
      case Some(port) =>
        Uri.from(scheme = server.scheme, host = server.host, port = port, path = path, queryString = query)
      case _          =>
        Uri.from(scheme = server.scheme, host = server.host, path = path, queryString = query)
    }
  }
}

extension (s: String) {
  def isUUID: Boolean = {
    Try(UUID.fromString(s)) match
      case Failure(_) => false
      case Success(_) => true
  }
}
