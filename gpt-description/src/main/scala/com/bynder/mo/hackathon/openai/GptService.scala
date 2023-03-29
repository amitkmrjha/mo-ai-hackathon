package com.bynder.mo.hackathon.openai

import com.theokanning.openai.service.OpenAiService
import com.theokanning.openai.completion.CompletionRequest
import com.theokanning.openai.completion.chat.*
import com.theokanning.openai.image.CreateImageRequest

import scala.jdk.CollectionConverters.*

object GptService {
  def withToken(token: String): GptService =
    GptService(new OpenAiService(token))
}

class GptService(val svc: OpenAiService) {

  def getSuggestion(tags: Seq[String]): Seq[String] = {
    val tagString = tags.mkString(" ")
    val prompt = s"Generate a tagline for an image described by tags ${tagString}"
    val message = ChatMessage("user", prompt)
    val req: ChatCompletionRequest = ChatCompletionRequest.builder().messages(List(message).asJava).model("gpt-3.5-turbo").n(3).build
    svc.createChatCompletion(req).getChoices.asScala.map(_.getMessage.getContent).toSeq
  }
}
