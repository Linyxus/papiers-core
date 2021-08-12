package papiers.core

import cats.implicits._
import cats.effect._

import MonadApp._
import java.io.File

/** Paper stores the metadata of a paper. */
case class Paper
  ( id: Int
  , title: String
  , authors: List[AuthorName]
  , venue: Option[String]
  , year: Option[String]
  , pages: Option[String]
  , conferencePaper: Boolean = false
  ) {
  def authorShorthand: String = authors match {
    case Nil => "unknown"
    case x :: _ => s"${x.surname} et al."
  }

  def authorList: String = authors match {
    case Nil => "unknown"
    case xs => authors map { case AuthorName(surname, givenName) => s"$givenName $surname" } mkString ", "
  }

  override def toString: String =
    val venueText = venue map (", " ++ _) getOrElse ""
    val yearText = year map (" (" ++ _ ++ ")") getOrElse ""
    val pagesText = pages map (": " ++ _) getOrElse ""
    s"$title, $authorShorthand$venueText$yearText$pagesText"

  def showDetails: String =
    s"  Id: $id" +
      s"\n\n  Title: $title" +
      s"\n\n  authors: $authorList" +
      s"\n\n  venue: ${venue getOrElse "unknown"}" +
      s"\n\n  year: ${year getOrElse "unknown"}" +
      s"\n\n  pages: ${pages getOrElse "unknown"}" +
      s"\n\n  type: ${if conferencePaper then "Conference Paper" else "Informal"}"

  def toJson: String = {
    import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
    this.asJson.noSpaces
  }
}

object Paper {
  def fromJson(json: String): Either[AppError, Paper] = {
    import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

    decode[Paper](json) match {
      case Left(e) => Left(JsonDecodeError(e.toString))
      case Right(x) => Right(x)
    }
  }
}

