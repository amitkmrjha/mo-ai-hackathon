package com.bynder.mo.hackathon.asset.api.routes

import akka.Done
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, MediaTypes, Multipart, StatusCodes}
import akka.http.scaladsl.server.ContentNegotiator.Alternative.ContentType
import akka.http.scaladsl.server.Directives.{concat, entity, withSizeLimit}
import akka.http.scaladsl.server.directives.BasicDirectives.*
import akka.http.scaladsl.server.directives.CodingDirectives.*
import akka.http.scaladsl.server.directives.FileUploadDirectives.*
import akka.http.scaladsl.server.directives.FutureDirectives.*
import akka.http.scaladsl.server.directives.MarshallingDirectives.*
import akka.http.scaladsl.server.directives.MethodDirectives.*
import akka.http.scaladsl.server.directives.PathDirectives.*
import akka.http.scaladsl.server.directives.RouteDirectives.*
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.stream.scaladsl.{Flow, Framing, Keep, Sink, Source}
import akka.stream.{Materializer, scaladsl}
import akka.util.ByteString
import com.bynder.clarify.ai.common.logging.CorrelationId
import com.bynder.mo.hackathon.asset.model.HackathonAssetFormat

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait UploadImageRuleRoute extends HackathonAssetFormat {

  given typedSystem: ActorSystem[?]
  given executionContext: ExecutionContext
  given exceptionHandler: ExceptionHandler

  def inclusionRuleRoute()(using correlationId: CorrelationId): Route =
    pathPrefix("inclusion") {
      concat(uploadInclusionList())
    }

  def uploadInclusionList()(using correlationId: CorrelationId): Route =
    pathEndOrSingleSlash {
      post {
        extractRequestContext { reqCtx =>
          val mediaType = reqCtx.request.entity.contentType.mediaType
          val mediaStr  = mediaType.mainType + "/" + mediaType.subType
          mediaStr match {
            case "multipart/form-data" =>
              withSizeLimit(1024 * 1024 * 60) {
               complete("ok")
              }
            case unsupportedType       =>
              complete(StatusCodes.BadRequest, "Bad Request: Unsupported request data type: " + unsupportedType)
          }
        }
      }
    }
}
