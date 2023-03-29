package com.bynder.mo.hackathon.asset

import akka.actor.typed.scaladsl.{Behaviors, LoggerOps}
import akka.actor.typed.{Behavior, SpawnProtocol}

object HackathonAssetBehavior:
  def apply(): Behavior[SpawnProtocol.Command] =
    Behaviors.setup { context =>
      SpawnProtocol()
    }
