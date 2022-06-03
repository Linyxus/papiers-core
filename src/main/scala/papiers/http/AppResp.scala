package papiers.http

import cats.effect.IO
import cats.implicits._
import io.circe.{Decoder, Encoder}
import io.circe._
import org.http4s.Method._
import org.http4s._
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.client.dsl.io._
import org.http4s.implicits._

import papiers.io._
import papiers.tools.Syntax._
import papiers.app._
import papiers.core._
import cats.data.AppFuncInstances

trait AppResp extends PaperInstances {
  case class PaperList(papers: List[Paper])

  given Encoder[PaperList] = Encoder.AsObject.derived[PaperList]
  given EntityEncoder[IO, PaperList] = jsonEncoderOf

  case class PaperSummary(summary: String)
  given Encoder[PaperSummary] = Encoder.AsObject.derived[PaperSummary]
  given EntityEncoder[IO, PaperSummary] = jsonEncoderOf

  case class PaperBib(bib: String)
  given Encoder[PaperBib] = Encoder.AsObject.derived[PaperBib]
  given EntityEncoder[IO, PaperBib] = jsonEncoderOf

  case class PaperPdf(pdfPath: String)
  given Encoder[PaperPdf] = Encoder.AsObject.derived[PaperPdf]
    given EntityEncoder[IO, PaperPdf] = jsonEncoderOf
}

object AppResp extends AppResp

