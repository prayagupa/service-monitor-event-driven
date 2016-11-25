package service

import scala.concurrent.{ExecutionContext, Future}
import sys.process._

/**
  * Created by prayagupd
  * on 11/23/16.
  */

class Event(val message: String)

case class Stopped() extends Event("Server is stopped")

case class Starting() extends Event("Server is starting")

case class Running() extends Event("Server is Running")


class HealthService {

  val serviceName = "[org.apache.catalina.startup].Bootstrap"
  val startService = "/usr/local/apache-tomcat-7.0.73/bin/startup.sh"

  def health(implicit ec: ExecutionContext): Future[Event] = {

    val serviceStatus = isServiceRunning

    println("Service is " + serviceStatus)

    serviceStatus match {
      case st: Stopped => {
        Starting
        Seq("/bin/sh", "-c", startService).!!
      }
      case _: Running => {}
    }

    Future {
      isServiceRunning
    }
  }

  def isServiceRunning: Event = {
    if (Seq("/bin/sh", "-c", "ps aux | grep \"" + serviceName + "\" | awk '{print $2}'").!!.trim.nonEmpty)
      Running()
    else
      Stopped()
  }

  def apply: HealthService = new HealthService()
}
