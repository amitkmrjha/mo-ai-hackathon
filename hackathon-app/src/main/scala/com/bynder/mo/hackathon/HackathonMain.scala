package com.bynder.mo.hackathon

import akka.Done
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.util.Timeout
import com.bynder.mo.hackathon.route.HackathonApi
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.*
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

object HackathonMain:

  lazy val config = ConfigFactory.load()

  given system: ActorSystem[?] = ActorSystem(Behaviors.empty, "hackathon", config)
  given ec: ExecutionContext                       = system.executionContext

  lazy val log = system.log

  @main def main(): Unit =
    sys.addShutdownHook(system.terminate())

    try {
      init(system)
    } catch {
      case NonFatal(e) =>
        log.error("Terminating due to initialization error", e)
        system.terminate()
    }

  def init(system: ActorSystem[?]): Unit =
    given ActorSystem[?] = system
    given ExecutionContext = system.executionContext

    lazy val host = config.getString("mo.hackathon.http.host")
    lazy val port = config.getInt("mo.hackathon.http.port")

    val api            = new HackathonApi()
    val bindingFuture  = Http()
      .newServerAt(host, port)
      .bind(api.routes)
    system.log.info(s"Hackathon API Server running at $host:$port")