package com.bynder.mo.http.utils

import com.bynder.mo.http.client.ServerToken
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import java.util.UUID

class IsUUIDSpec extends AsyncFlatSpec with Matchers:
  "isUUID" should "return true" in {
    val testString = UUID.randomUUID().toString
    testString.isUUID shouldEqual true
  }

  it should "return false" in {
    val testString = "anotherstring"
    testString.isUUID shouldEqual false
  }
