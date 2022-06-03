package papiers.http

import cats.effect._
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.io._

import papiers.http._

object AppRoutes extends AppResp {
  def testRoutes: HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "hello" => Ok("hello!")
    }

  def coreRoutes: HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "ls" =>
        ReqHandler.listPapers.value flatMap {
          case Left(err) => InternalServerError(err.toString)
          case Right(papers) => Ok(papers)
        }
    }
}

