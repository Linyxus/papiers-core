package papiers.core

import cats._
import cats.implicits._
import papiers.tools.Syntax._

trait StrParser[+T] {
  def parse(s: String): Option[T]
}

object StrParser {
  given StrParser[Int] with
    override def parse(s: String): Option[Int] = s.toIntOption

  given StrParser[String] with
    override def parse(s: String): Option[String] = Some(s)

  given StrParser[Boolean] with
    override def parse(s: String): Option[Boolean] = s match
      case "true" | "True" | "t" => Some(true)
      case "false" | "False" | "f" => Some(false)
      case _ => None

  given StrParser[List[AuthorName]] with
    override def parse(s: String): Option[List[AuthorName]] =
      val allNames = (s split ",").toList map { s => s.strip } map { name =>
        name.split("\\s+").toList match
          case Nil => None
          case _ :: Nil => None
          case xs =>
            Some(AuthorName(surname = xs.last, givenName = xs.init mkString " "))
      }
      allNames.chainM
}