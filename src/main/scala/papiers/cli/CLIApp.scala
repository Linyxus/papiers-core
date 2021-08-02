package papiers.cli

import cats.effect._
import cats.implicits._
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
    s"[$i]: ${bundle.paper}"

  def printLibrary(lib: Map[Int, PaperBundle]): AppM[Unit] = MonadApp.liftIO {
    val strs = lib.toList.sortWith({ (a, b) => a._1 < b._1 }).map { (i, bundle) => showPaperBundle(i, bundle) }

    IO.println(strs mkString "\n")
  }

  def handleListPapers(listPapers: ListPapers): AppM[Unit] = listPapers match {
    case ListPapers(_) =>
      loadLibrary flatMap printLibrary
  }

  def handlePaperInfo(getPaperInfo: GetPaperInfo): AppM[Unit] = getPaperInfo match {
    case GetPaperInfo(pid, getPdf, _, getSum) =>
      loadLibrary.flatMap { lib =>
        lib.get(pid) match {
          case None => MonadApp.throwError(CLIError(s"paper id does not exist: $pid"))
          case Some(PaperBundle(meta, pdf)) =>
            MonadApp.liftIO {
              if getPdf then
                IO.println(pdf.toPath.toAbsolutePath.toString)
              else if getSum then
                IO.println(meta.toString)
              else
                IO.println(meta.showDetails)
            }
        }
      }
  }

  def handleImportPaper(cmd: ImportPaper): AppM[Unit] = cmd match {
    case ImportPaper(pdfPath) =>
      Config.getLibraryDir >>= { libDir => Library.importPaper(libDir, pdfPath).asUnit }
  }

  def handleCommand(cmd: AppCommand): IO[ExitCode] = cmd match {
    case cmd: ListPapers => handleListPapers(cmd).execute
    case cmd: GetPaperInfo => handlePaperInfo(cmd).execute
    case cmd: ImportPaper => handleImportPaper(cmd).execute
    case _ => IO { ExitCode.Error }
  }
}

object CLIApp extends CLIApp