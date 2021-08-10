package papiers.io

import cats.effect._
import cats.data.EitherT
import cats.implicits._
import sttp.client3._
import sttp.model.Uri
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import org.json4s._
import native.JsonMethods.parse

import papiers.core.MonadApp
import MonadApp._

trait DblpClient {
  def rawGet(url: Uri): IO[Either[String, String]] =
    AsyncHttpClientCatsBackend.resource[IO]() use { backend =>
      val req = basicRequest.get(url)

      val resp = req.send(backend)

      resp map { resp =>
        resp.body
      }
    }

  def get(url: Uri): AppM[String] =
    def liftIO(m: IO[Either[String, String]]): AppM[String] =
      val m1 = m map {
        case Left(err) => Left(IOError(s"network error: $err"))
        case Right(x) => Right(x)
      }
      EitherT { m1 }

    liftIO(rawGet(url))

  def getJson(url: Uri): AppM[JValue] =
    get(url)
      .map(parse(_))
      .handleErrorWith(err => MonadApp.throwError(IOError(s"can not parse json: $err")))
}

object DblpClient extends DblpClient
