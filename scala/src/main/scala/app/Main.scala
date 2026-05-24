package app

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import app.modelos.{LoginReq, JsonSupport}
import app.servicios.AuthService
import scala.util.{Failure, Success}

object Main extends JsonSupport {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "EveaAPI")
    implicit val ec = system.executionContext
    
    val authService = new AuthService()

    val rutas = pathPrefix("api") {
      path("login") {
        post {
          entity(as[LoginReq]) { req =>
            onComplete(authService.login(req)) {
              case Success(Right(res)) => complete(res)
              case Success(Left(err))  => complete(401 -> err)
              case Failure(ex)         => complete(500 -> s"Error: ${ex.getMessage}")
            }
          }
        }
      }
    }

    Http().newServerAt("localhost", 8080).bind(rutas)
    println("Bloque 1 (Transaccional) corriendo en http://localhost:8080/")
  }
}