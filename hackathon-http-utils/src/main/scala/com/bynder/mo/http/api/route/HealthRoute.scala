package com.bynder.mo.http.api.route

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import com.bynder.clarify.ai.common.logging.CorrelationId
import com.bynder.mo.http.api.model.HealthResponse
import com.bynder.mo.http.utils.MOJsonFormat
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.{Content, Schema}
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.{Operation, Parameter}
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.{GET, Path, Produces}
import spray.json.DefaultJsonProtocol

import java.time.Instant

trait HealthRoute extends MOJsonFormat:

  def health(using correlationId: CorrelationId): Route = health(None)

  @Path("/health")
  @GET
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(
    summary = "Health check",
    description = "Check the health of the service",
    responses = Array(
      new ApiResponse(
        responseCode = "200",
        description = "Healthy response",
        content = Array(new Content(schema = new Schema(implementation = classOf[HealthResponse])))
      ),
      new ApiResponse(responseCode = "500", description = "Unhealthy response")
    )
  )
  def health(message: Option[String])(using correlationId: CorrelationId): Route =
    path("health") {
      get {
        val health = HealthResponse(StatusCodes.OK, Instant.now, message.getOrElse("API is healthy."), Seq.empty)
        complete(StatusCodes.OK, health)
      }
    }
