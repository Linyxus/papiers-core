package papiers.cli

import cats.effect._
import cats.implicits._
import papiers.core._
import MonadApp._
import PropSetter.allSetters
import StrParser.given
import papiers.io._
import papiers.tools.Syntax._
import papiers.app._

import java.nio.file.Paths

trait CLIApp extends AppHandler {
  import AppCommand._

  def handleCommand(cmd: AppCommand): IO[ExitCode] = cmd match {
    case cmd: ListPapers => handleListPapers(cmd).execute
    case cmd: GetPaperInfo => handlePaperInfo(cmd).execute
    case cmd: ImportPaper => handleImportPaper(cmd).execute
    case cmd: DownloadPaper => handleDownloadPaper(cmd).execute
    case cmd: SetProp => handleSetProp(cmd).execute
    case cmd: MatchPaper => handleMatchPaper(cmd).execute
    case _ => IO { ExitCode.Error }
  }
}

object CLIApp extends CLIApp
