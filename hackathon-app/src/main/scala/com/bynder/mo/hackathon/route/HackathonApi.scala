package com.bynder.mo.hackathon.route

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route

import scala.concurrent.ExecutionContext

class HackathonApi(using val typedSystem: ActorSystem[?]) {

  given executionContext: ExecutionContext = typedSystem.executionContext

  val routes: Route = concat(postRoute)
  
  def postRoute: Route =
    path("image") { 
      get {
            complete(StatusCodes.OK, "nothing")
      }
    }

}
