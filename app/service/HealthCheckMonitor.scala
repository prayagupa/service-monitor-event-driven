package service

import javax.inject.Inject
import play.api.Play.current

import akka.actor.Actor
import akka.actor.Actor.Receive
import play.api.{Configuration, Play}

import scala.concurrent.{ExecutionContext, Future}
import sys.process._

/**
  * Created by prayagupd
  * on 11/23/16.
  */

class HealthCheckMonitor extends Actor {

  val service  = new MonitorService
  val startService = "/usr/local/apache-tomcat-7.0.73/bin/startup.sh"
  var received = 0

  override def receive: Receive = {
    case health: CheckHealthCommand => {
      received = received + 1
      println(s"Received CheckHealth ${received} times.")
      val serviceStatus = service.serviceStatusEvent(health)

      context.system.eventStream.publish(serviceStatus)

      println("HealthCheckMonitor# Service is " + serviceStatus)

      serviceStatus match {
        case st: Stopped => {
          context.system.eventStream.publish(new Starting())
          service.start(new StartServiceCommand(startService))
        }
        case r: Running => {}
      }
    }
  }
}
