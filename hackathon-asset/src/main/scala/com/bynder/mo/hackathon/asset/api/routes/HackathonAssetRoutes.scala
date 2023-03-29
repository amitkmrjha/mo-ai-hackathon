package com.bynder.mo.hackathon.asset.api.routes

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.{PathMatcher, Route}
import com.bynder.clarify.ai.common.logging.CorrelationId
import com.bynder.mo.http.api.model.HealthResponse
import com.bynder.mo.http.api.route.HealthRoute
import com.bynder.mo.http.utils.MOJsonFormat
import jakarta.ws.rs.Path
import org.slf4j.Logger
import spray.json.*

import java.time.Instant
import scala.concurrent.ExecutionContext

@Path("/mo/hackathon/ai/api")
trait HackathonAssetRoutes extends MOJsonFormat with HealthRoute:

  given typedSystem: akka.actor.typed.ActorSystem[?]
  given executionContext: ExecutionContext
  given log: Logger

  def moHackathonAIRoute(using CorrelationId): Route = concat(
    pathPrefix("mo" / "hackathon" /"ai" / "api") {
      concat(health(Some("MO Hackathon AI API is healthy")))
    }
  )
