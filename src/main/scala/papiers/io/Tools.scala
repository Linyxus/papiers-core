package papiers.io

import scala.io.Source

import cats.implicits._
import cats.effect._
import papiers.core.MonadApp
import MonadApp._

import java.io.File
import java.nio.file.Paths

/** A thin cats wrapper around fs utilities provided by jdk. */
object Tools {
  def joinPath(path: String, ext: String): String =
    Paths.get(path, ext).toAbsolutePath.toString

  def listFiles(dirPath: String): AppM[List[File]] =
    val errorF = (x: Throwable) => IOError(s"can not list directory $dirPath: ${x.toString}")

    safeIO(errorF) {
      val d = new File(dirPath)

      d.listFiles.toList flatMap { x =>
        if x.isFile then Some(x) else None
      }
    }
  end listFiles

  def getContent(f: File): AppM[String] =
    safeIO(err => IOError(s"can not read file content: $err")) {
      Source.fromFile(f).mkString
    }

  def parseName(expectExt: String)(f: File): Option[(Int, File)] = {
    val name = f.getName

    def getBaseExt: Option[(String, String)] = (name split "\\.").toList match {
      case base :: ext :: Nil if ext == expectExt =>
        Some(base, ext)
      case _ => None
    }

    def parseBaseExt(base: String, ext: String): Option[Int] =
      base.toIntOption

    getBaseExt flatMap { (base, ext) => parseBaseExt(base, ext) } map { i => (i, f) }
  }

  /** Parse pdf filename.
   * A valid pdf file should be in form INTEGER.pdf.
   */
  def parsePdfName(f: File): Option[(Int, File)] =
    parseName("pdf")(f)

  def listPdfs(dirPath: String): AppM[List[(Int, File)]] =
    listFiles(dirPath) map { xs => xs flatMap parsePdfName }

  /** Parse JSON metadata filename.
   * A valid metadata file should be in form INTEGER.json.
   *
   * @param f
   * @return
   */
  def parseMetaName(f: File): Option[(Int, File)] =
    parseName("json")(f)

  def listMetas(dirPath: String): AppM[List[(Int, File)]] =
    listFiles(dirPath) map { xs => xs flatMap parseMetaName }

  def checkFileExists(f: File): AppM[Boolean] =
    safeIO(err => IOError(s"can not check whether file exists: $err")) { f.exists() }

  def ensureFileExists(f: File): AppM[File] = f.safeCheckExists flatMap { exists =>
    if !exists then
      MonadApp.throwError(IOError(s"file not exist: ${f.getPath}"))
    else
      MonadApp.pure(f)
  }

  extension (f: File) {
    def safeCheckExists: AppM[Boolean] = checkFileExists(f)
    def ensureExists: AppM[File] = ensureFileExists(f)
  }
}
