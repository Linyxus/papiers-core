package papiers.cli

import cats.effect._
import papiers.core._
import MonadApp._
import papiers.io._

trait CLIApp {
  import AppCommand._

  def loadLibrary: AppM[Map[Int, PaperBundle]] =
    Config.getLibraryDir flatMap { dirPath =>
      Library.loadLibrary(dirPath)
    }

  def showPaperBundle(i: Int, bundle: PaperBundle): String =
    s"[$i]: ${bundle.paper}\n\n    PDF: ${bundle.pdf.toPath.toString}\n"

  def printLibrary(lib: Map[Int, PaperBundle]): AppM[Unit] = MonadApp.liftIO {
    val strs = lib.toList.map { (i, bundle) => showPaperBundle(i, bundle) }

    IO.println(strs mkString "\n")
  }

  def handleListPapers(listPapers: ListPapers): AppM[Unit] = listPapers match {
    case ListPapers(_) =>
      loadLibrary flatMap printLibrary
  }

  def handleCommand(cmd: AppCommand): IO[ExitCode] = cmd match {
    case cmd: ListPapers => handleListPapers(cmd).execute
    case _ => IO { ExitCode.Success }
  }
}

object CLIApp extends CLIApp