package controllers

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorSystem, Props}
import play.api.mvc._
import service._

import scala.concurrent.ExecutionContext.Implicits.global

class Application extends Controller {

  val serviceMonitorSystem = ActorSystem("ServiceMonitor")
  val serviceMonitor = serviceMonitorSystem.actorOf(Props[HealthCheckMonitor], name = "serviceMonitor")
  val uiActor = serviceMonitorSystem.actorOf(Props[UiActor], name = "uiActor")

  val serviceName = "[org.apache.catalina.startup].Bootstrap"

  def health = Action {

    val checkHealth = new CheckHealthCommand(serviceName)

    new Thread(new Runnable {
      override def run(): Unit =  {
        while(true) {
          serviceMonitor ! checkHealth
        }
      }
    }).start()

    Results.Ok(new MonitorService().serviceStatusEvent(checkHealth).message)
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
