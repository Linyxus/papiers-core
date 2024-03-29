package papiers.cli

import cats.effect._
import cats.implicits._
import cats.data.Validated

import papiers.app._

import com.monovore.decline._
import com.monovore.decline.effect._

import com.comcast.ip4s._

import java.nio.file.Path

object CLIParser {
  import AppCommand._
  import CliCommand._

  val runDaemonOpts: Opts[RunDaemon] =
    Opts.subcommand("daemon", "Run Http daemon") {
      val port = Opts.option[String]("port", "Port to listen on.", short = "p").orNone

      port mapValidated {
        case None => Validated.valid(RunDaemon(port"8990"))
        case Some(port) =>
          Port.fromString(port) match {
            case None => Validated.invalidNel(s"Invalid port: $port")
            case Some(port) => Validated.valid(RunDaemon(port))
          }
      }
    }

  val listPapersOpts: Opts[ListPapers] =
    Opts.subcommand("ls", "List all papers") {
      val query = Opts.arguments[String]("PAPER").orNone map {
        case None => Nil
        case Some(x) => x.toList
      }
      val collection = Opts.option[String]("collection", "List papers in a collection.", short = "c").orNone

      (query, collection) mapN { (q, c) => ListPapers(q, c) }
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

  val downloadPaper: Opts[DownloadPaper] =
    Opts.subcommand("dl", "Download and import a paper from url") {
      val pdfPath = Opts.argument[String]("URL")
      val arxiv = Opts.flag("arxiv", help = "Import arxiv paper", short = "-a").orFalse

      (pdfPath, arxiv) mapN DownloadPaper.apply
    }

  val setPropParser: Opts[SetProp] =
    Opts.subcommand("set", "Set the metadata of a paper") {
      val key = Opts.argument[String]("KEY")
      val pid = Opts.argument[Int]("PAPER")
      val value = Opts.argument[String]("VALUE")

      (pid, key, value) mapN { (pid, key, value) => SetProp(pid, key, value) }
    }

  val matchPaperParser: Opts[MatchPaper] =
    Opts.subcommand("match", "match the metadata of a paper on Dblp") {
      val paper = Opts.argument[Int]("PAPER")

      paper map { pid => MatchPaper(pid) }
    }

  val syncBibParser: Opts[SyncBib] =
    Opts.subcommand("sync-bib", "Sync the .bib bundle file")(Opts.unit).map(_ => SyncBib())

  val commandParser: Opts[AppCommand | CliCommand] =
    listPapersOpts orElse getPaperInfo orElse importPaper orElse setPropParser orElse matchPaperParser orElse downloadPaper orElse runDaemonOpts orElse syncBibParser
}

