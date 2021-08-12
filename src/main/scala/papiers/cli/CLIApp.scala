package papiers.cli

import cats.effect._
import cats.implicits._
import papiers.core._
import MonadApp._
import PropSetter.allSetters
import StrParser.given
import papiers.io._
import papiers.tools.Syntax._

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

  def handleSetProp(cmd: SetProp): AppM[Unit] = cmd match
    case SetProp(pid, k, v) =>
      loadLibrary >>= { lib =>
        def getPaper: AppM[Paper] = lib get pid match
          case None => MonadApp.throwError(CLIError(s"paper id does not exist: $pid"))
          case Some(PaperBundle(meta, pdf)) => MonadApp.pure { meta }

        def getSetter: AppM[PropSetter] = allSetters get k match
          case None => MonadApp.throwError(CLIError(s"can not set property: $k"))
          case Some(setter) => MonadApp.pure { setter }

        (getPaper, getSetter).tupleM >>= { (paper, setter) =>
          setter.setProp(paper, v) match
            case None => MonadApp.throwError(CLIError(s"can not parse value: $v"))
            case Some(paper) =>
              import Tools._
              Config.getLibraryDir >>= { libDir => paper.writeTo(libDir) }
        }
      }

  def handleMatchPaper(cmd: MatchPaper): AppM[Unit] = cmd match
    case MatchPaper(pid) =>
      loadLibrary >>= { lib =>
        def getPaper: AppM[Paper] = lib get pid match
          case None => MonadApp.throwError(CLIError(s"paper id does not exist: $pid"))
          case Some(PaperBundle(meta, pdf)) => MonadApp.pure { meta }

        getPaper >>= DblpClient.matchPaper >>= { p =>
          import Tools._
          MonadApp.liftIO {
            IO.println(p.showDetails)
          } >> { Config.getLibraryDir >>= { libDir => p.writeTo(libDir) } }
        }
      }

  def handleCommand(cmd: AppCommand): IO[ExitCode] = cmd match {
    case cmd: ListPapers => handleListPapers(cmd).execute
    case cmd: GetPaperInfo => handlePaperInfo(cmd).execute
    case cmd: ImportPaper => handleImportPaper(cmd).execute
    case cmd: SetProp => handleSetProp(cmd).execute
    case cmd: MatchPaper => handleMatchPaper(cmd).execute
    case _ => IO { ExitCode.Error }
  }
}

object CLIApp extends CLIApp