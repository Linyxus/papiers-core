package papiers.cli

import cats.effect._
import cats.implicits._

import com.monovore.decline._
import com.monovore.decline.effect._

import java.nio.file.Path

object CLIParser {
  import AppCommand._

  val listPapersOpts: Opts[ListPapers] =
    Opts.subcommand("ls", "List all papers") {
      Opts.option[String]("collection", "List papers in a collection.", short = "c").orNone map ListPapers.apply
    }

  val getPaperInfo: Opts[GetPaperInfo] =
    Opts.subcommand("info", "Get detailed information of the paper") {
      // Opts.argument[Int]("PAPER_ID") map GetPaperInfo.apply
      val pid = Opts.argument[Int]("PAPER_ID")
      val getPdf = Opts.flag("pdf", help = "Retrieve PDF path.", short = "-p").orFalse
      val getBib = Opts.flag("bib", help = "Retrieve bibtex.", short = "-b").orFalse
      val getSummary = Opts.flag("sum", help = "Retrieve summary.", short = "-s").orFalse

      (pid, getPdf, getBib, getSummary) mapN { (pid, pdf, bib, sum) => GetPaperInfo(pid, pdf, bib, sum) }
    }

  val importPaper: Opts[ImportPaper] =
    Opts.subcommand("add", "Import a new paper") {
      val pdfPath = Opts.argument[Path]("FILE")

      pdfPath map ImportPaper.apply
    }

  def setPropParser: Opts[SetProp] =
    Opts.subcommand("set", "Set the metadata of a paper") {
      val key = Opts.argument[String]("KEY")
      val pid = Opts.argument[Int]("PAPER")
      val value = Opts.argument[String]("VALUE")

      (pid, key, value) mapN { (pid, key, value) => SetProp(pid, key, value) }
    }

  val commandParser: Opts[AppCommand] =
    listPapersOpts orElse getPaperInfo orElse importPaper orElse setPropParser
}

