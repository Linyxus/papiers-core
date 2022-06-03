package papiers.http

import cats.effect._
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.io._

object AppRoutes {
  def testRoutes: HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "hello" => Ok("hello!")
    }
}

