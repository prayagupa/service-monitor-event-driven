package service

import akka.actor.Actor
import akka.actor.Actor.Receive

import scala.concurrent.{ExecutionContext, Future}
import sys.process._

/**
  * Created by prayagupd
  * on 11/23/16.
  */

class HealthCheckMonitor extends Actor {

  val service  = new MonitorService
  val startService = "/usr/local/apache-tomcat-7.0.73/bin/startup.sh"

  override def receive: Receive = {
    case health: CheckHealthCommand => {

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
