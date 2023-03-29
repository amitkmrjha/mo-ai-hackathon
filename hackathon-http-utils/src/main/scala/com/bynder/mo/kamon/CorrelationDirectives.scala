package com.bynder.mo.kamon

import akka.http.scaladsl.server.Directives.{mapInnerRoute, optionalHeaderValueByName}
import akka.http.scaladsl.server.{Directive0, Directive1, Rejection}
import com.bynder.clarify.ai.common.logging.CorrelationId
import kamon.Kamon
import kamon.context.Context

import java.util.UUID

object CorrelationDirectives:

  def withCorrelationId: Directive1[CorrelationId] = {
    optionalHeaderValueByName("x-api-correlation-id").map(header =>
      header match {
        case Some(id) => id
        case _        => UUID.randomUUID().toString
      }
    )
  }

  private val correlationIdKey =
    Context.key[String]("correlationId", "undefined")

  def configureSpanName(methodName: String): Directive0 =
    mapInnerRoute { route => ctx =>
      val response = route(ctx)

      val span = Kamon.currentSpan()
      span.name(s"${methodName} ${span.operationName()}")
      response
    }

  def configureCorrelationContext(correlationId: String): Directive0 =
    mapInnerRoute { route => ctx =>
      Kamon.runWithContextEntry(
        correlationIdKey,
        correlationId.toString()
      ) {
        val response = route(ctx)

        val span = Kamon.currentSpan()
        span.tag("correlationId", correlationId.toString())
        response
      }
    }
