package models

case class Basket(name: String, sign: String, quantity: Int)

case class Order(number: Int, name: String, sign: String, quantity: Int, status: Int)

case class Product(name: String, description: String, price: Int)

case class User(sign: String, password: String, name: String, family: String)
