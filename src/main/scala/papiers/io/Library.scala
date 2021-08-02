package papiers.io

import cats.implicits._
import cats.effect._
import papiers.core.MonadApp._
import papiers.core.{Paper, PaperBundle}

import java.io.File
import java.nio.file.{Path, Paths, Files, StandardCopyOption}
import papiers.core.MonadApp

object Library {
  import papiers.tools.Syntax._
  import Tools._

  def getSubdir(subname: String)(baseDir: String): String =
    Paths.get(baseDir, subname).toAbsolutePath.toString

  val getPdfDir: String => String = getSubdir("pdf")
  val getMetaDir: String => String = getSubdir("meta")

  /** Initialize library directories. */
  def initLibrary(baseDir: String): AppM[Unit] = {
    val errorBuilder = (x: Throwable) => IOError(s"can not initialize library: $x")

    def mkDirsIfNotExists(f: File): AppM[Unit] =
      safeIO(errorBuilder) {
        if !f.exists then
          f.mkdirs
      }

    val dir = new File(baseDir)
    val pdfDir = new File(getPdfDir(baseDir))
    val metaDir = new File(getMetaDir(baseDir))

    val effect: AppM[Unit] =
      for
        _ <- mkDirsIfNotExists(dir)
        _ <- mkDirsIfNotExists(pdfDir)
        _ <- mkDirsIfNotExists(metaDir)
      yield
        ()

    effect
  }

  private def loadPaper(jsonFile: File): AppM[Paper] =
    Tools.getContent(jsonFile) flatMap { s => Paper.fromJson(s).liftToAppM }

  def loadLibrary(baseDir: String): AppM[Map[Int, PaperBundle]] =
    (Tools.listPdfs(getPdfDir(baseDir)), Tools.listMetas(getMetaDir(baseDir))).tupleM flatMap { 
      (pdfList, metaList) => 
        val pdfMap = Map.from(pdfList)
        val metaMap = Map.from(metaList)
        val validIdx = pdfMap.keySet intersect metaMap.keySet
        
        val papersM: List[AppM[(Int, PaperBundle)]] = validIdx.toList map { i =>
          loadPaper(metaMap(i)) map { pap => i -> PaperBundle(pap, pdfMap(i)) }
        }

        papersM.chainM map Map.from
    }

  /** Get available paper id in the library. */
  def getAvailableId(baseDir: String): AppM[Int] =
    loadLibrary(baseDir) map (_.keySet.max + 1)

  /** Import one paper into the library. */
  def importPaper(baseDir: String, pdfPath: Path): AppM[Int] =
    pdfPath.toFile.ensureExists.flatMap { pdfFile =>
      getAvailableId(baseDir) flatMap { pid =>
        def copyPdf: AppM[Unit] = {
          val pdfDir = getPdfDir(baseDir)
          val destPath = Paths.get(Tools.joinPath(pdfDir, s"$pid.pdf"))

          Tools.copyFile(pdfPath, destPath)
        }

        def createMeta: AppM[Unit] =
          Pdf.getPdfTitle(pdfPath.toFile) flatMap { pdfTitle =>
            Paper(
              id = pid,
              title = pdfTitle,
              authors = Nil,
              venue = None,
              year = None,
              pages = None
            ).writeTo(baseDir)
          }

        copyPdf >> createMeta >> MonadApp.pure(pid)
      }
    }
}

