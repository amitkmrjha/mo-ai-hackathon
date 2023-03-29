package com.bynder.mo.http.client

import akka.http.scaladsl.model.StatusCode

sealed trait BynderServerError
case class NotFoundException(message: String)      extends Exception(message) with BynderServerError
case class AccessDeniedException(message: String)  extends Exception(message) with BynderServerError
case class UnauthorizedException(message: String)  extends Exception(message) with BynderServerError
case class InternalErrorException(message: String) extends Exception(message) with BynderServerError
case class BadRequestException(message: String)    extends Exception(message) with BynderServerError
case class GenericHttpException(message: String, statusCode: StatusCode)
    extends Exception(message)
    with BynderServerError
