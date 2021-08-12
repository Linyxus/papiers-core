package papiers.io

import cats._
import cats.implicits._
import org.json4s._

import papiers.tools.Syntax._

case class DblpResponse(
  title: String, 
  authors: List[String], 
  venue: String, year: String, pages: Option[String], 
  informal: Boolean
)


object DblpResponse:
  def fromBodyJson(json: JValue): List[DblpResponse] = json.selectAsList(List("result", "hits", "hit")) match
    case None => Nil
    case Some(hits) =>
      def buildFromEntry(value: JValue): Option[DblpResponse] =
        def cleanAuthorName(name: String): String =
          name.split("\\s+").toList match {
            case xs if xs.length >= 3 =>
              if xs.last.toIntOption.isDefined then
                xs.dropRight(1).mkString(" ")
              else
                name
            case _ => name
          }

        def getTitle = value.selectAsString(List("info", "title"))
        def getVenue = value.selectAsString(List("info", "venue"))
        def getYear = value.selectAsString(List("info", "year"))
        def getInformal = value.selectAsString(List("info", "type")) map (_ == "Informal Publications")
        def getPages = value.selectAsString(List("info", "pages"))
        def getAuthors = value.selectAsList(List("info", "authors", "author")) map { authors =>
          authors flatMap { author => author.selectAsString(List("text")) map cleanAuthorName }
        }

        for
          title <- getTitle
          venue <- getVenue
          year <- getYear
          informal <- getInformal
          pages = getPages
          authors <- getAuthors
        yield
          DblpResponse(title, authors, venue, year, pages, informal)
      
      hits flatMap buildFromEntry
