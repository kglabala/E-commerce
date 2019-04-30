package dao

import javax.inject.Inject
import models.Order
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

class OrderDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile]
{
  import driver.api._

  private val Orders = TableQuery[OrdersTable]

  def all(): Future[Seq[Order]] = db.run(Orders.result)

  def insert(order: Order): Future[Unit] = db.run(Orders += order).map { _ => () }

  def delete(number: Int): Future[Unit] = db.run(Orders.filter(_.number === number).delete).map(_ => ())

  def findById(number: Int): Future[Option[Order]] = db.run(Orders.filter(_.number === number).result.headOption)

  def update(number: Int, order: Order): Future[Unit] = {
    val orderToUpdate: Order = order.copy(number)
    db.run(Orders.filter(_.number === number).update(orderToUpdate)).map(_ => ())
  }

  private class OrdersTable(tag: Tag) extends Table[Order](tag, "ORDER") {

    def number = column[Int]("NUMBER", O.PrimaryKey, O.AutoInc)

    def sign = column[String]("SIGN")

    def name = column[String]("NAME")

    def quantity = column[Int]("QUANTITY")

    def status = column[Int]("STATUS")

    def * = (number, sign, name, quantity, status) <>(Order.tupled, Order.unapply _)
  }
}
