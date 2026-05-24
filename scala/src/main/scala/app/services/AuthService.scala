package app.services

import app.modelos.{Usuario, UsuariosTable, LoginReq, AuthRes}
import slick.jdbc.PostgresProfile.api._
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import scala.concurrent.{Future, ExecutionContext}
import java.time.Clock

class AuthService(implicit ec: ExecutionContext) {
  
  // Conecta usando el application.conf
  private val db = Database.forConfig("mysql_db")
  private val secretKey = "clave_secreta_evea"
  private val tablaUsuarios = TableQuery[UsuariosTable]

  def login(req: LoginReq): Future[Either[String, AuthRes]] = {
    
    val query = tablaUsuarios.filter(_.email === req.email).result.headOption
    
    db.run(query).map {
      // Simplificado: en un entorno real compara hashes (ej. con BCrypt)
      case Some(user) if user.passwordHash == req.password => 
        Right(AuthRes(generarJWT(user), user.id))
      case _ => 
        Left("Credenciales incorrectas")
    }
  }

  private def generarJWT(u: Usuario): String = {
    implicit val clock: Clock = Clock.systemUTC
    val claim = JwtClaim(
      content = s"""{"usuario_id":"${u.id}", "rol":"${u.rol}"}""",
      expiration = Some(System.currentTimeMillis() / 1000 + 3600) // 1 hora
    )
    Jwt.encode(claim, secretKey, JwtAlgorithm.HS256)
  }
}