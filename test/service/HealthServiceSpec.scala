package service

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
  * Created by prayagupd
  * on 11/23/16.
  */

@RunWith(classOf[JUnitRunner])
class HealthServiceSpec extends Specification {
  "health" should {
    "return server status as false when service is not running" in {
      val status = new HealthService().health
      Await.result(status, Duration("10 seconds")) mustEqual(Stopped())
    }

    "return server status as true when service is running" in {
      val status = new HealthService().health
      Await.result(status, Duration("10 seconds")) mustEqual(Running())
    }
  }
}
