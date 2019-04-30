package dao

import javax.inject.Inject
import models.Basket
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

class BasketDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile]
{
  import driver.api._

  private val Baskets = TableQuery[BasketsTable]

  def all(): Future[Seq[Basket]] = db.run(Baskets.result)

  def insert(basket: Basket): Future[Unit] = db.run(Baskets += basket).map { _ => () }

  def delete(sign: String, name: String): Future[Unit] = db.run(Baskets.filter(b => b.sign === sign && b.name === name).delete).map(_ => ())

  def findById(sign: String, name: String): Future[Option[Basket]] = db.run(Baskets.filter(b => b.sign === sign && b.name === name).result.headOption)

  def update(sign: String, name: String, basket: Basket): Future[Unit] = {
    val basketToUpdate: Basket = basket.copy(sign, name)
    db.run(Baskets.filter(_.sign === sign).update(basketToUpdate)).map(_ => ())
  }

  private class BasketsTable(tag: Tag) extends Table[Basket](tag, "BASKET") {

    def sign = column[String]("SIGN", O.PrimaryKey)

    def name = column[String]("NAME", O.PrimaryKey)

    def quantity = column[Int]("QUANTITY")

    def * = (sign, name, quantity) <> (Basket.tupled, Basket.unapply _)
  }
}
