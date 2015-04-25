package web

import akka.actor.ActorSystem
import akka.event.Logging

import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl.Sink
import com.typesafe.config.ConfigFactory
import domain.model.Person
import domain.persistance.Tables
import slick.driver.H2Driver.api._
import spray.json.{pimpAny, DefaultJsonProtocol}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.Future

object AkkaHttp extends App with DefaultJsonProtocol {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorFlowMaterializer()

  val config = ConfigFactory.load()
  val logger = Logging(system, getClass)

  val db = Database.forConfig("h2mem1")

  implicit val personFormat = jsonFormat3(Person.apply)

  val personsQuery = Tables.persons.result

  val personsInserter = (Tables.persons returning Tables.persons.map(_.id)) += _

  val routes = path("persons") {
    get {
      complete {
        db.run(personsQuery)
      }
    } ~
      (post & entity(as[Person])) { person =>
        complete{
          db.run(personsInserter(person)).map[String](_.toString)
        }
    }
  }

import akka.http.scaladsl.model.HttpMethods.GET

  val requestHandler: HttpRequest => HttpResponse = {
    case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
      HttpResponse(
        entity = HttpEntity(MediaTypes.`text/html`,
          "<html><body>Hello world!</body></html>"))

    case HttpRequest(GET, Uri.Path("/ping"), _, _, _)  => HttpResponse(entity = "PONG!")
    case HttpRequest(GET, Uri.Path("/crash"), _, _, _) => sys.error("BOOM!")
    case _: HttpRequest                                => HttpResponse(404, entity = "Unknown resource!")
  }


//  Route.handlerFlow(routes)
//  val combined = requestHandler >> routes



// BELOW LIES THE RUNNING AND WIRING
  val dbSetUp = Tables.persons.schema.create >> (Tables.persons += Person(0, "name", 21))

  db.run(dbSetUp).onSuccess {
//    case _ => Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
    case _ => val serverSource = Http().bind(config.getString("http.interface"), config.getInt("http.port"))
      serverSource.to(Sink.foreach{ connection =>
//        connection.handleWithSyncHandler(requestHandler)
        // does not like having both on here. only uses the top one.
        // Not sure how to combine/filter these
        connection.handleWith(routes)


      }).run()
  }



}