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
}

object AppResp extends AppResp

