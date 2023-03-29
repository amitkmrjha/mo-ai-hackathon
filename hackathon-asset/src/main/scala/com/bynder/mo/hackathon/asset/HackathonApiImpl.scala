package com.bynder.mo.hackathon.asset

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import com.bynder.clarify.ai.common.logging.CorrelationId
import com.bynder.mo.hackathon.asset.api.routes.HackathonAssetRoutes
import com.bynder.mo.kamon.CorrelationDirectives.*
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.Logger

import scala.concurrent.ExecutionContext

class HackathonApiImpl(using val typedSystem: ActorSystem[?]) extends HackathonAssetRoutes:
  given executionContext: ExecutionContext = typedSystem.executionContext
  given log: Logger                        = typedSystem.log

  val routes: Route =
    extractMethod { method =>
      configureSpanName(methodName = method.name()) {
        withCorrelationId { cId =>
          given correlationId: CorrelationId = cId

          configureCorrelationContext(correlationId = cId) {
            moHackathonAIRoute
          }
        }
      }
    }
