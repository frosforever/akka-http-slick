package domain.persistance

import domain.model.Person
import org.scalatest.FunSuite
import org.scalatest.concurrent.ScalaFutures
import slick.driver.H2Driver.api._
import slick.jdbc.meta._

import scala.concurrent.duration._

class TablesTest extends FunSuite with ScalaFutures {
  implicit override val patienceConfig = PatienceConfig(timeout = 5.seconds)

  def dbFixture(test: Database => Unit) = {
    val db = Database.forConfig("h2mem1")
    try {
      test(db)
    } finally {
      db.close()
    }
  }

  test("something")(dbFixture { db =>
    db.run(Tables.persons.schema.create).futureValue

    val insertCount = db.run(Tables.persons += Person(0, "name", 10)).futureValue

    val tables = db.run(MTable.getTables).futureValue

    assert(tables.size === 1)
    assert(tables.count(_.name.name.equalsIgnoreCase("persons")) == 1)

    assert(insertCount === 1)

    // TableQuery[T].result doesn't seem to show up even though this compiles and works
    val retrieved: Seq[Person] = db.run(Tables.persons.result).futureValue

    assert(retrieved.head.name === "name")
  })
}
