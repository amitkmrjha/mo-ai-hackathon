package com.bynder.mo.http.api.model

import akka.http.scaladsl.model.StatusCode
import io.swagger.v3.oas.annotations.media.{ArraySchema, Schema}

import java.time.Instant

// Note: @Schema to convert the swagger doc to Int and not the StatusCode class that has
//       other members we don't wish to show
final case class HealthResponse(
    @Schema(`type` = "integer", format = "int32") code: StatusCode,
    timestamp: Instant,
    message: String,
    @ArraySchema(schema = new Schema(implementation = classOf[ServicesHealthResponse])) services: Seq[
      ServicesHealthResponse
    ]
)
