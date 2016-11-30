package service

/**
  * Created by prayagupd
  * on 11/25/16.
  */

trait Event

class BaseEvent(val message: String) extends Event

case class CheckHealthCommand(val serviceName : String) extends BaseEvent("checks the service")

case class Stopped() extends BaseEvent(message = "Server is stopped")

case class Starting() extends BaseEvent(message = "Server is starting")

case class Running() extends BaseEvent(message = "Server is Running")
