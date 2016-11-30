package service

import sys.process._

/**
  * Created by prayagupd
  * on 11/25/16.
  */

case class StartServiceCommand(val startService : String) extends BaseEvent("Start the service")

class MonitorService {

  def serviceStatusEvent(event: CheckHealthCommand): BaseEvent = {
    val service = "ps aux | grep \"" + event.serviceName + "\" | awk '{print $2}'"
    println(s"MonitorService : $service")
    if (Seq("/bin/sh", "-c", service).!!.trim.nonEmpty)
      new Running()
    else
      new Stopped()
  }

  def start(command: StartServiceCommand): Unit = {
    Seq("/bin/sh", "-c", command.startService).!!
  }
}
