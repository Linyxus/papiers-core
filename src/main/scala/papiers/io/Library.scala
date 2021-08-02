package papiers.io

import cats.implicits._
import cats.effect._
import papiers.core.MonadApp._
import papiers.core.{Paper, PaperBundle}

import java.io.File
import java.nio.file.Paths

object Library {
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

  private def loadPaper(jsonFile: File): AppM[Paper] = {
    Tools.getContent(jsonFile) flatMap { s => Paper.fromJson(s) }
  }

  def loadLibrary(baseDir: String): AppM[Map[Int, PaperBundle]] =
    ???
}

