package com.bynder.clarify.ai.common.logging

type CorrelationId = String

object CorrelationId:
  def apply(s: String): CorrelationId = s

extension (id: CorrelationId) def toString: String = id.toString
