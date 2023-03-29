package com.bynder.mo.hackathon.asset

import akka.Done
import akka.actor.typed.scaladsl.AskPattern.*
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Props, SpawnProtocol}
import akka.http.scaladsl.Http
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import kamon.Kamon

import scala.concurrent.duration.*
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

object HackathonAssetMain:
  lazy val config = ConfigFactory.load()

  Kamon.init(config)

  given system: ActorSystem[SpawnProtocol.Command] = ActorSystem(HackathonAssetBehavior(), "hackathon-ai", config)
  given ec: ExecutionContext                       = system.executionContext

  lazy val log = system.log

  @main def main(): Unit =
    system.log.info(s"MO Enhance server running ")

    sys.addShutdownHook(system.terminate())

    try {

      val initResult = for {
        _ <- init(system)
      } yield Done
      initResult.onComplete {
        case Success(_)     =>
          log.info(s"Successfully Started  hackathon-ai service")
        case Failure(error) =>
          log.error(s"Unable to start hackathon-ai service", error)
          system.terminate()
      }
    } catch {
      case NonFatal(e) =>
        log.error("Terminating due to initialization error", e)
        system.terminate()
    }

  def init(system: ActorSystem[?]) =
    given ActorSystem[?] = system
    lazy val host        = config.getString("mo.enhance.api.http.host")
    lazy val port        = config.getInt("mo.enhance.api.http.port")

    val api           = new HackathonApiImpl()
    val bindingFuture = Http()
      .newServerAt(host, port)
      .bind(api.routes)
    system.log.info(s"MO Enhance API Server running at $host:$port")
    bindingFuture
