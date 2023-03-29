package com.bynder.mo.http.client

import akka.actor.Scheduler
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.client.RequestBuilding.*
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import akka.http.scaladsl.marshalling.*
import akka.http.scaladsl.model.*
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.http.scaladsl.{Http, HttpsConnectionContext}
import com.bynder.mo.http.utils.*
import org.slf4j.{Logger, LoggerFactory}
import spray.json.*

import java.net.UnknownHostException
import scala.compiletime.ops.boolean.||
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

class RestClient(using system: ActorSystem[?]) {
  val log: Logger = LoggerFactory.getLogger(this.getClass.getName)

  given ExecutionContext = system.classicSystem.dispatcher
  given Scheduler        = system.classicSystem.scheduler

  def getResource[T, U](path: String, unwrapFunction: T => U)(using um: Unmarshaller[ResponseEntity, T])(using
      ServerToken
  ): Future[U] =
    getResource(path, Option.empty, unwrapFunction)

  def getResource[T, U](path: String, query: Option[String], unwrapFunction: T => U)(using
      um: Unmarshaller[ResponseEntity, T]
  )(using ServerToken): Future[U] = {
    val uri     = Uri.forPath(path, query)
    val request = Get(uri = uri).withToken
    log.debug("Retrieving resource from uri {}", uri)
    for {
      response       <- Http().singleRequest(request) recoverWith { case e: Exception =>
                          handleRequestFailure(e, uri)
                        }
      resourceFuture <- response.status match
                          case StatusCodes.OK =>
                            log.trace("Successfully retrieved resource.")
                            for {
                              responseModel <- Unmarshal(response.entity).to[T]
                            } yield unwrapFunction(responseModel)
                          case _              => handleHttpErrorStatus(response, uri)
    } yield resourceFuture
  }

  def putResource[A, T](path: String, resource: A)(using
      um: Unmarshaller[ResponseEntity, T]
  )(using m: Marshaller[A, RequestEntity])(using ServerToken): Future[T] = {
    putResource(path, None, resource, identity[A], identity[T])
  }

  def putResource[A, B, T, U](
      path: String,
      query: Option[String],
      resource: A,
      requestBodyConverter: A => B,
      responseBodyConverter: T => U
  )(using um: Unmarshaller[ResponseEntity, T])(using m: Marshaller[B, RequestEntity])(using ServerToken): Future[U] = {
    sendResource(Put, path, query, resource, requestBodyConverter, responseBodyConverter)
  }

  def putResource[A, T](path: String, query: Option[String], resource: A)(using
      um: Unmarshaller[ResponseEntity, T]
  )(using m: Marshaller[A, RequestEntity])(using ServerToken): Future[T] = {
    putResource(path, query, resource, identity[A], identity[T])
  }

  def postResource[A, T](path: String, resource: A)(using
      um: Unmarshaller[ResponseEntity, T]
  )(using m: Marshaller[A, RequestEntity])(using ServerToken): Future[T] = {
    postResource(path, None, resource, identity[A], identity[T])
  }

  def postResource[A, T](path: String, query: Option[String], resource: A)(using
      um: Unmarshaller[ResponseEntity, T]
  )(using m: Marshaller[A, RequestEntity])(using ServerToken): Future[T] = {
    postResource(path, query, resource, identity[A], identity[T])
  }

  def postResource[A, B, T, U](
      path: String,
      query: Option[String],
      resource: A,
      requestBodyConverter: A => B,
      responseBodyConverter: T => U
  )(using um: Unmarshaller[ResponseEntity, T])(using m: Marshaller[B, RequestEntity])(using ServerToken): Future[U] = {
    sendResource(Post, path, query, resource, requestBodyConverter, responseBodyConverter)
  }

  def sendResource[A, B, T, U](
      method: RequestBuilder,
      path: String,
      query: Option[String],
      resource: A,
      requestBodyConverter: A => B,
      responseBodyConverter: T => U
  )(using um: Unmarshaller[ResponseEntity, T])(using m: Marshaller[B, RequestEntity])(using ServerToken): Future[U] = {
    val uri     = Uri.forPath(path, query)
    val request = method(uri = uri, requestBodyConverter(resource)).withToken
    log.debug("Retrieving resource from uri {}", uri)
    for {
      response       <- Http().singleRequest(request) recoverWith { case e: Exception =>
                          handleRequestFailure(e, uri)
                        }
      resourceFuture <- response.status match
                          case StatusCodes.OK | StatusCodes.Created =>
                            log.trace("Successfully sent resource.")
                            for {
                              responseModel <- Unmarshal(response.entity).to[T]
                            } yield responseBodyConverter(responseModel)
                          case _                                    => handleHttpErrorStatus(response, uri)
    } yield resourceFuture
  }

  def deleteResource[A](path: String, requestBody: Option[A] = Option.empty)(using
      m: ToEntityMarshaller[A]
  )(using ServerToken): Future[Boolean] = {
    val uri = Uri.forPath(path, None)
    log.debug("Deleting resource from uri {}", uri)
    for {
      response       <- Http().singleRequest(Delete(uri = uri, content = requestBody).withToken) recoverWith {
                          case e: Exception => handleRequestFailure(e, uri)
                        }
      resourceFuture <- response.status match
                          case StatusCodes.OK | StatusCodes.NoContent => Future.successful(true)
                          case _                                      => handleHttpErrorStatus(response, uri)
    } yield resourceFuture
  }

  protected def handleRequestFailure[T](e: Throwable, uri: Uri): Future[T] = e.getCause match {
    case c: UnknownHostException => Future.failed(NotFoundException(s"Invalid host $uri"))
    case _                       => Future.failed(e)
  }

  protected def handleHttpErrorStatus[T](response: HttpResponse, uri: Uri): Future[T] = {
    val status      = response.status
    val errorStatus = s"Request failed with error $status."
    status match
      case StatusCodes.BadRequest          =>
        log.error("Failed to retrieve resource {}: Bad request", uri)
        Future.failed(BadRequestException(errorStatus))
      case StatusCodes.NotFound            =>
        log.error("Failed to retrieve resource {}: Not found", uri)
        Future.failed(NotFoundException(errorStatus))
      case StatusCodes.Unauthorized        =>
        log.error("Failed to retrieve resource {}: Unauthorized", uri)
        Future.failed(UnauthorizedException(errorStatus))
      case StatusCodes.Forbidden           =>
        log.error("Failed to retrieve resource {}: Forbidden", uri)
        Future.failed(AccessDeniedException(errorStatus))
      case StatusCodes.InternalServerError =>
        log.error("Failed to retrieve resource {}: Bynder Internal Error", uri)
        Future.failed(InternalErrorException(errorStatus))
      case _                               =>
        log.error("Failed to retrieve resource {}", uri)
        Future.failed(GenericHttpException(errorStatus, status))
  }
}
