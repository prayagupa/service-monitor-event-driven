package controllers

import play.api.mvc._
import service.HealthService

import scala.concurrent.ExecutionContext.Implicits.global

class Application extends Controller {

  def health = Action.async {
    new HealthService().health.map(x =>
      Ok(x.message)
    )
  }

}