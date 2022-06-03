package papiers.http

import cats.effect._
import cats.implicits._
import org.http4s._
import org.http4s.HttpRoutes
import org.http4s.dsl.io._

import papiers.core._
import papiers.http._
import MonadApp._

object AppRoutes extends AppResp {
  def testRoutes: HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "hello" => Ok("hello!")
    }

  def adaptApp[X](appM: AppM[X])(using EntityEncoder[IO, X]) =
    appM.value flatMap {
      case Left(err) => InternalServerError(err.toString)
      case Right(resp) => Ok(resp)
    }

  extension[X] (m: AppM[X]) {
    def adaptHttp(using EntityEncoder[IO, X]) = adaptApp(m)
  }

  def coreRoutes: HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "ls" =>
        ReqHandler.listPapers.adaptHttp

      case GET -> Root / "info" / "sum" / IntVar(paperId) =>
        ReqHandler.getSummary(paperId).adaptHttp

      case GET -> Root / "info" / "bib" / IntVar(paperId) =>
        ReqHandler.getBib(paperId).adaptHttp

      case GET -> Root / "info" / "pdf" / IntVar(paperId) =>
        ReqHandler.getPdf(paperId).adaptHttp
    }
}

