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

import papiers.core._

trait PaperInstances {
  given Decoder[AuthorName] = Decoder.derived[AuthorName]
  given EntityDecoder[IO, AuthorName] = jsonOf
  given Encoder[AuthorName] = Encoder.AsObject.derived[AuthorName]
  given EntityEncoder[IO, AuthorName] = jsonEncoderOf

  given Decoder[Paper] = Decoder.derived[Paper]
  given EntityDecoder[IO, Paper] = jsonOf
  given Encoder[Paper] = Encoder.AsObject.derived[Paper]
  given EntityEncoder[IO, Paper] = jsonEncoderOf
}

object PaperInstances extends PaperInstances

