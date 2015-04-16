package domain.persistance

import domain.model.Person
import slick.driver.H2Driver.api._

class Persons(tag: Tag) extends Table[Person](tag, "PERSONS") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

  def name = column[String]("NAME")

  def age = column[Int]("AGE")

  def * = (id, name, age) <> (Person.tupled, Person.unapply)
}

object Tables {
  val persons = TableQuery[Persons]
}

