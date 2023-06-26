package com.bynder.mo.hackathon.openai

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GptServiceSpec extends AnyFlatSpec with Matchers {
  val token:String = System.getenv("GPT_TOKEN")

  "service" should "return some suggestions" in {
    val svc = GptService.withToken(token)
    val r = svc.getSuggestion(Seq("sock", "blue", "apparel", "stripes"))
    r.foreach(println)
    r.length shouldEqual 3

  }
}
