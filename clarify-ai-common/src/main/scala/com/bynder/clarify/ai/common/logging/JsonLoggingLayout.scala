package com.bynder.clarify.ai.common.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.contrib.jackson.JacksonJsonFormatter
import ch.qos.logback.contrib.json.classic.JsonLayout

import java.io.IOException

class JsonLoggingLayout extends JsonLayout:

  override def addCustomDataToJsonMap(map: java.util.Map[String, Object], event: ILoggingEvent): Unit =

    val correlationId = map
      .getOrDefault("mdc", new java.util.HashMap[String, String]())
      .asInstanceOf[java.util.Map[String, String]]
      .getOrDefault("correlationId", "")

    if (correlationId.nonEmpty) {
      map.put("correlation_id", correlationId)
    }

    map.put("log_level", map.get("level"))
    map.remove("level")
