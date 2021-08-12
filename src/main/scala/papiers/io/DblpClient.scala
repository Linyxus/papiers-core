package papiers.io

import cats.effect._
import cats.data.EitherT
import cats.implicits._
import sttp.client3._
import sttp.model.Uri
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import org.json4s._
import native.JsonMethods.parse

import papiers.core.{MonadApp, Paper, PropSetter}
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

  def query(title: String): AppM[List[DblpResponse]] =
    def buildQueryUri: Uri = uri"https://dblp.org/search/publ/api?q=$title&h=1000&format=json"

    getJson(buildQueryUri) map DblpResponse.fromBodyJson

  def findBestMatch(title: String, matches: List[DblpResponse]): Option[DblpResponse] =
    /** Filter out the matches whose title does not match the expected title exactly */
    def isExactMatch(t1: String, t2: String): Boolean =
      val words1 = t1.toLowerCase.split("\\s+")
      val words2 = t2.toLowerCase.split("\\s+")
      words1.length == words2.length && (words1 zip words2 forall { (w1, w2) => (w1 startsWith w2) || (w2 startsWith w1) })

    def betterThan(p1: DblpResponse, p2: DblpResponse): Boolean =
      !p1.informal && p2.informal

    (matches filter { p => isExactMatch(title, p.title) } sortWith betterThan).headOption

  def matchTitle(title: String): AppM[DblpResponse] = query(title) flatMap { matches =>
    findBestMatch(title, matches) match
      case None => MonadApp.throwError(IOError(s"could not find match for $title, candidates: $matches"))
      case Some(m) => MonadApp.pure(m)
  }

  def updatePaperWithMatch(p: Paper, resp: DblpResponse): AppM[Paper] =
    import PropSetter._
    def getPaper1 = authorSetter.setProp(p, resp.authors mkString ", ") match
      case None => MonadApp.throwError(IOError(s"could not parse authors: ${resp.authors mkString ", "}"))
      case Some(p) => MonadApp.pure(p)

    getPaper1 map { paper1 =>
      paper1.copy(
        venue = Some(resp.venue),
        year = Some(resp.year),
        pages = resp.pages,
        conferencePaper = !resp.informal
      )
    }

  def matchPaper(p: Paper): AppM[Paper] =
    matchTitle(p.title) >>= { resp => updatePaperWithMatch(p, resp) }
}

object DblpClient extends DblpClient
