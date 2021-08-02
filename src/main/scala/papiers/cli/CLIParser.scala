package papiers.cli

import cats.effect._
import cats.implicits._

import com.monovore.decline._
import com.monovore.decline.effect._

object CLIParser {
  import AppCommand._

  val listPapersOpts: Opts[ListPapers] =
    Opts.subcommand("ls", "List all papers") {
      Opts.option[String]("collection", "List papers in a collection.", short = "c").orNone map ListPapers.apply
    }

  val getPaperBib: Opts[GetPaperBib] =
    Opts.subcommand("bib", "Get bibtex entry of the paper") {
      Opts.option[String]("paper", "The paper to retrieve bib", short = "-p") map GetPaperBib.apply
    }

  val getPaperPdf: Opts[GetPaperPdf] =
    Opts.subcommand("pdf", "Get associated PDF file path of the paper") {
      Opts.option[String]("paper", "The paper to retrieve pdf", short = "-p") map GetPaperPdf.apply
    }

  val commandParser: Opts[AppCommand] =
    listPapersOpts orElse getPaperBib orElse getPaperPdf
}

