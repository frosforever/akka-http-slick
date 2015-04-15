package web

import akka.event.{NoLogging, LoggingAdapter}
import akka.http.testkit.ScalatestRouteTest
import com.typesafe.config.Config
import org.scalatest.{Matchers, FlatSpec}
import akka.http.model.StatusCodes._
import akka.http.model.ContentTypes._

class ServiceTest extends FlatSpec with Matchers with ScalatestRouteTest with Service {
  override def config: Config = testConfig

  override val logger: LoggingAdapter = NoLogging

  "Service" should "respond with hello" in {
    Get("/") ~> routes ~> check {
      status shouldBe OK
      contentType shouldBe `text/plain(UTF-8)`
    }
  }
}
