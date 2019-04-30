package controllers

import dao.{BasketDAO, OrderDAO, ProductDAO, UserDAO}
import models.{Basket, Order, Product, User}
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.text
import play.api.data.Forms.number
import play.api.mvc._
import javax.inject.Inject
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class Application @Inject() (basketDAO: BasketDAO, orderDAO: OrderDAO, productDAO: ProductDAO, userDAO: UserDAO) extends Controller {

	val basketForm = Form(
		mapping(
			"sign" -> text(),
			"name" -> text(),
			"quantity" -> number
		)(Basket.apply)(Basket.unapply)
	)

	val orderForm = Form(
		mapping(
			"number" -> number,
			"sign" -> text(),
			"name" -> text(),
			"quantity" -> number,
			"status" -> number
		)(Order.apply)(Order.unapply)
	)

	val productForm = Form(
		mapping(
			"name" -> text(),
			"description" -> text(),
			"price" -> number
		)(Product.apply)(Product.unapply)
	)

	val userForm = Form(
		mapping(
			"sign" -> text(),
			"password" -> text(),
			"name" -> text(),
			"family" -> text()
		)(User.apply)(User.unapply)
	)

	def index = Action {
		Ok(views.html.index.render())
	}

	def listbaskets = Action.async {
		basketDAO.all().map(baskets => Ok(views.html.listbaskets(baskets)))
	}

	def listorders = Action.async {
		orderDAO.all().map(orders => Ok(views.html.listorders(orders)))
	}

	def listproducts = Action.async {
		productDAO.all().map(products => Ok(views.html.listproducts(products)))
	}

	def listusers = Action.async {
		userDAO.all().map(users => Ok(views.html.listusers(users)))
	}

	def addbasket = Action.async {
		productDAO.all().flatMap{products => userDAO.all().map{users => Ok(views.html.addbasket.render(products, users))}}
	}

	def addorder = Action.async {
		productDAO.all().flatMap{products => userDAO.all().map{users => Ok(views.html.addorder.render(products, users))}}
	}

	def addproduct = Action {
		Ok(views.html.addproduct.render())
	}

	def adduser = Action {
		Ok(views.html.adduser.render())
	}

	def newbasket = Action { implicit request =>
		val basket: models.Basket = basketForm.bindFromRequest().get
		basketDAO.insert(basket)
		Ok(views.html.newbasket.render(basket))
	}

	def neworder = Action { implicit request =>
		val order: models.Order = orderForm.bindFromRequest().get
		orderDAO.insert(order)
		Ok(views.html.neworder.render(order))
	}

	def newproduct = Action { implicit request =>
		val product: models.Product = productForm.bindFromRequest().get
		productDAO.insert(product)
		Ok(views.html.newproduct.render(product))
	}

	def newuser = Action { implicit request =>
		val user: models.User = userForm.bindFromRequest().get
		userDAO.insert(user)
		Ok(views.html.newuser.render(user))
	}

	def deletebasket(sign: String, name: String) = Action { implicit request =>
		basketDAO.delete(sign, name)
		Redirect(routes.Application.listbaskets())
	}

	def deleteorder(number: Int) = Action { implicit request =>
		orderDAO.delete(number)
		Redirect(routes.Application.listorders())
	}

	def deleteproduct(name: String) = Action { implicit request =>
		productDAO.delete(name)
		Redirect(routes.Application.listproducts())
	}

	def deleteuser(sign: String) = Action { implicit request =>
		userDAO.delete(sign)
		Redirect(routes.Application.listusers())
	}

	def updatebasket(sign: String, name: String) = Action.async { implicit request =>
		val computerAndOptions = for {
			basket <- basketDAO.findById(sign, name)
		} yield (basket)

		computerAndOptions.map { case (computer) =>
			computer match {
				case Some(basket) => Ok(views.html.updatebasket(basket))
				case None => NotFound
			}
		}
	}

	def updateorder(number: Int) = Action.async { implicit request =>
		val computerAndOptions = for {
			order <- orderDAO.findById(number)
		} yield (order)

		computerAndOptions.map { case (computer) =>
			computer match {
				case Some(order) => Ok(views.html.updateorder(order))
				case None => NotFound
			}
		}
	}

	def updateproduct(name: String) = Action.async { implicit request =>
		val computerAndOptions = for {
			product <- productDAO.findById(name)
		} yield (product)

		computerAndOptions.map { case (computer) =>
			computer match {
				case Some(product) => Ok(views.html.updateproduct(product))
				case None => NotFound
			}
		}
	}

	def updateuser(sign: String) = Action.async { implicit request =>
		val computerAndOptions = for {
			user <- userDAO.findById(sign)
		} yield (user)

		computerAndOptions.map { case (computer) =>
			computer match {
				case Some(user) => Ok(views.html.updateuser(user))
				case None => NotFound
			}
		}
	}

	def applybasket = Action { implicit request =>
		val updatedBasket: models.Basket = basketForm.bindFromRequest().get
		basketDAO.update(updatedBasket.sign, updatedBasket.name, updatedBasket)
		Redirect(routes.Application.listbaskets())
	}

	def applyorder = Action { implicit request =>
		val updatedOrder: models.Order = orderForm.bindFromRequest().get
		orderDAO.update(updatedOrder.number, updatedOrder)
		Redirect(routes.Application.listorders())
	}

	def applyproduct = Action { implicit request =>
		val updatedProduct: models.Product = productForm.bindFromRequest().get
		productDAO.update(updatedProduct.name, updatedProduct)
		Redirect(routes.Application.listproducts())
	}

	def applyuser = Action { implicit request =>
		val updatedUser: models.User = userForm.bindFromRequest().get
		userDAO.update(updatedUser.sign, updatedUser)
		Redirect(routes.Application.listusers())
	}
}