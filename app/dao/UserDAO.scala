package dao

import javax.inject.Inject
import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

class UserDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile]
{
  import driver.api._

  private val Users = TableQuery[UsersTable]

  def all(): Future[Seq[User]] = db.run(Users.result)

  def insert(sign: User): Future[Unit] = db.run(Users += sign).map { _ => () }

  def delete(sign: String): Future[Unit] = db.run(Users.filter(_.sign === sign).delete).map(_ => ())

  def findById(sign: String): Future[Option[User]] = db.run(Users.filter(_.sign === sign).result.headOption)

  def update(sign: String, user: User): Future[Unit] = {
    val userToUpdate: User = user.copy(sign)
    db.run(Users.filter(_.sign === sign).update(userToUpdate)).map(_ => ())
  }

  private class UsersTable(tag: Tag) extends Table[User](tag, "USER") {

    def sign = column[String]("SIGN", O.PrimaryKey)

    def password = column[String]("PASSWORD")

    def name = column[String]("NAME")

    def family = column[String]("FAMILY")

    def * = (sign, password, name, family) <>(User.tupled, User.unapply _)
  }
}
