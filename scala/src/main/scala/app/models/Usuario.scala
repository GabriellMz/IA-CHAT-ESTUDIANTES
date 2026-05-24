package app.models

import slick.jdbc.PostgresProfile.api._
import spray.json.DefaultJsonProtocol._

// 1. Case class inmutable
case class Usuario(id: String, nombre: String, email: String, rol: String, passwordHash: String)

// 2. Mapeo exacto a tu tabla en Supabase
class UsuariosTable(tag: Tag) extends Table[Usuario](tag, "usuarios") {
  def id = column[String]("id", O.PrimaryKey)
  def nombre = column[String]("nombre")
  def email = column[String]("email", O.Unique)
  def rol = column[String]("rol")
  def passwordHash = column[String]("password_hash")

  def * = (id, nombre, email, rol, passwordHash) <> (Usuario.tupled, Usuario.unapply)
}

// 3. Formatos para I/O (Login y Token)
case class LoginReq(email: String, password: String)
case class AuthRes(token: String, usuario_id: String)

trait JsonSupport {
  implicit val loginReqFormat = jsonFormat2(LoginReq)
  implicit val authResFormat = jsonFormat2(AuthRes)
}