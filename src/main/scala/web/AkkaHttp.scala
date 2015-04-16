package web

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.Http
import akka.http.server.Directives._
import akka.stream.ActorFlowMaterializer
import com.typesafe.config.ConfigFactory
import domain.model.Person
import domain.persistance.Tables
import slick.driver.H2Driver.api._
import spray.json.DefaultJsonProtocol
import akka.http.marshallers.sprayjson.SprayJsonSupport._

object AkkaHttp extends App with DefaultJsonProtocol {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorFlowMaterializer()

  val config = ConfigFactory.load()
  val logger = Logging(system, getClass)

  val db = Database.forConfig("h2mem1")

  implicit val personFormat = jsonFormat3(Person.apply)

  val routes = path("person") {
    get {
      complete {
        db.run(Tables.persons.result)
      }
    } ~
      (post & entity(as[Person])) { person =>
        complete{
          db.run((Tables.persons returning Tables.persons.map(_.id)) += person).map[String](_.toString)
        }
    }
  }

  val dbSetUp = Tables.persons.schema.create >> (Tables.persons += Person(0, "name", 21))



  db.run(dbSetUp).onSuccess {
    case _ => Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
  }

}