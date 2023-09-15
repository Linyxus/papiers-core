package papiers.core

import scala.collection.mutable.StringBuilder

object BibPrinter extends BibPrinter {
  def showBib(meta: Paper, filePath: Option[String] = None): String = {
    /** Print author name in the form of surname, given name. */
    def showAuthor(author: AuthorName): String = s"${author.surname}, ${author.givenName}"

    def showAuthors(author: List[AuthorName]) =
      author map showAuthor mkString " and "

    def getBibId(meta: Paper): String =
      val sur = meta.authors.headOption map { author => author.surname } getOrElse "unknown"
      val year = meta.year getOrElse "0000"
      s"$sur$year"

    def aliasVenue(venue: String): String =
      if venue == "CoRR" then "arXiv" else venue

    /** Print the metadata of a formal publication to bib. */
    def show(meta: Paper): String = {
      val sb = StringBuilder()

      def pubType = if meta.conferencePaper then "inproceedings" else "article"

      sb ++= s"@${pubType}{${getBibId(meta)},\n"
      sb ++= s"author = {${showAuthors(meta.authors)}},\n"

      meta.year foreach { year => sb ++= s"year = {${year}},\n" }
      meta.venue map aliasVenue foreach { venue => sb ++= s"booktitle = {${venue}},\n" }
      meta.pages foreach { pages => sb ++= s"pages = {${pages}},\n" }

      filePath foreach { fp => sb ++= s"pdf = {$fp},\n" }

      sb ++= s"title = {${meta.title}}\n}\n"

      sb.toString
    }

    show(meta)
  }

  def showBib(bundle: List[PaperBundle], includePdfPath: Boolean): String = {
    val bibStrs = bundle.map { case PaperBundle(paper, pdf) =>
      showBib(paper, if includePdfPath then Some(pdf.toPath.toAbsolutePath.toString) else None)
    }
    bibStrs.mkString("\n\n")
  }
}

trait BibPrinter {
  import BibPrinter._

  extension (p: Paper) {
    def toBib: String = showBib(p)
  }
}

