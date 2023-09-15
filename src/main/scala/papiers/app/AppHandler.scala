package papiers.app

import cats.effect._
import cats.implicits._
import papiers.core.BibPrinter
import papiers.core._
import MonadApp._
import PropSetter.allSetters
import StrParser.given
import papiers.io._
import papiers.tools.Syntax._

import java.nio.file.Paths

trait AppHandler extends BibPrinter:
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
    case ListPapers(qs, _) =>
      def paperFilter: PaperFilter = PaperFilter.product(qs map PaperFilter.titleFilter)
      loadLibrary map { lib => lib.filter { (k, v) => paperFilter(v.paper) } } flatMap printLibrary
  }

  def handlePaperInfo(getPaperInfo: GetPaperInfo): AppM[Unit] = getPaperInfo match {
    case GetPaperInfo(pid, getPdf, getBib, getSum) =>
      loadLibrary.flatMap { lib =>
        lib.get(pid) match {
          case None => MonadApp.throwError(CommandError(s"paper id does not exist: $pid"))
          case Some(PaperBundle(meta, pdf)) =>
            MonadApp.liftIO {
              if getPdf then
                IO.println(pdf.toPath.toAbsolutePath.toString)
              else if getSum then
                IO.println(meta.toString)
              else if getBib then
                IO.println(meta.toBib)
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

  private def downloadPaper(url: String): AppM[Unit] =
    def savePath(libDir: String): String = Tools.joinPath(libDir, "temp.pdf")

    def download(libDir: String): AppM[Unit] =
      Download.downloadFile(url, savePath(libDir))

    def doImport(libDir: String): AppM[Unit] =
      val p = Paths.get(savePath(libDir))
      Library.importPaper(libDir, p).asUnit

    Config.getLibraryDir >>= { libDir =>
      download(libDir) >> doImport(libDir)
    }

  def handleDownloadPaper(cmd: DownloadPaper): AppM[Unit] = cmd match
    case DownloadPaper(url, true) =>
      val realUrl = s"https://arxiv.org/pdf/$url.pdf"
      downloadPaper(realUrl)
    case DownloadPaper(url, _) => downloadPaper(url)

  def handleSetProp(cmd: SetProp): AppM[Unit] = cmd match
    case SetProp(pid, k, v) =>
      loadLibrary >>= { lib =>
        def getPaper: AppM[Paper] = lib get pid match
          case None => MonadApp.throwError(CommandError(s"paper id does not exist: $pid"))
          case Some(PaperBundle(meta, pdf)) => MonadApp.pure { meta }

        def getSetter: AppM[PropSetter] = allSetters get k match
          case None => MonadApp.throwError(CommandError(s"can not set property: $k"))
          case Some(setter) => MonadApp.pure { setter }

        (getPaper, getSetter).tupleM >>= { (paper, setter) =>
          setter.setProp(paper, v) match
            case None => MonadApp.throwError(CommandError(s"can not parse value: $v"))
            case Some(paper) =>
              import Tools._
              Config.getLibraryDir >>= { libDir => paper.writeTo(libDir) }
        }
      }

  def handleMatchPaper(cmd: MatchPaper): AppM[Unit] = cmd match
    case MatchPaper(pid) =>
      loadLibrary >>= { lib =>
        def getPaper: AppM[Paper] = lib get pid match
          case None => MonadApp.throwError(CommandError(s"paper id does not exist: $pid"))
          case Some(PaperBundle(meta, pdf)) => MonadApp.pure { meta }

        getPaper >>= DblpClient.matchPaper >>= { p =>
          import Tools._
          MonadApp.liftIO {
            IO.println(p.showDetails)
          } >> { Config.getLibraryDir >>= { libDir => p.writeTo(libDir) } }
        }
      }

  def handleSyncBib(cmd: SyncBib): AppM[Unit] =
     loadLibrary.map(lib => lib.values.toList) >>= Library.outputBibBundle
