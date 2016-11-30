package controllers

import javax.inject.Inject

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorSystem, Props}
import play.api.Configuration
import play.api.mvc._
import service._

import scala.concurrent.ExecutionContext.Implicits.global

class Application @Inject()(configuration: Configuration) extends Controller {

  val serviceMonitorSystem = ActorSystem("ServiceMonitor")
  val serviceMonitor = serviceMonitorSystem.actorOf(Props[HealthCheckMonitor], name = "serviceMonitor")
  val uiActor = serviceMonitorSystem.actorOf(Props[UiActor], name = "uiActor")

  val serviceName = configuration.getString("service.name").get

  monitor()

  def health = Action {
    println("Check Health")
    val status = new MonitorService().serviceStatusEvent(new CheckHealthCommand(serviceName)).message
    println(s"status = $status")
    Results.Ok(s"{status : $status}")
  }

  def monitor(): Unit = {
    new Thread(new Runnable {
      override def run(): Unit =  {
        while(true) {
          serviceMonitor ! new CheckHealthCommand(serviceName)
        }
      }
    }).start()
  }
}

class UiActor extends Actor {

  override def preStart = context.system.eventStream.subscribe(self, classOf[BaseEvent])

  override def receive: Receive = {
    case event : Starting => println("UiActor : Server is Starting")
    case event: Stopped => println("UiActor : Server is stopped")
    case event: Running => println("UiActor : Server is Up")
    case _ => println("UiActor : I'm dead")
  }
}
