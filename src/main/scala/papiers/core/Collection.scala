package papiers.core

import cats.implicits._
import cats.effect._

import MonadApp._

case class Collection(id: Int, name: String)

object Collection:
  def fromJsonList(json: String): Either[AppError, List[Collection]] =
    import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

    decode[List[Collection]](json) match
      case Left(e) => Left(JsonDecodeError(e.toString))
      case Right(x) => Right(x)
