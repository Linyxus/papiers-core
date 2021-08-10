package papiers.io

import cats._
import cats.implicits._
import org.json4s._

case class DblpResponse(
  title: String, 
  authors: List[String], 
  venue: String, year: String, pages: Option[String], 
  informal: Boolean
)


object DblpResponse {
  def fromBodyJson(json: JValue): List[DblpResponse] = json match
    case JObject(fields) => ???
    case _ => Nil
}
