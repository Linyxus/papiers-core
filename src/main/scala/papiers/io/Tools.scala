package papiers.io

import scala.io.Source
import java.io.PrintWriter

import cats.implicits._
import cats.effect._
import papiers.core._
import MonadApp._

import java.io.File
import java.nio.file.{Path, Paths, Files, StandardCopyOption}

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

  def writeFile(content: String, path: String): AppM[Unit] =
    safeIO(err => IOError(s"can not write to file: $err")) {
      val out = new PrintWriter(new File(path), "UTF-8")
      try
        out.print(content)
      finally
        out.close()
    }

  def writeMeta(libDir: String, meta: Paper): AppM[Unit] =
    val metaDir = Library.getMetaDir(libDir)
    val metaPath = joinPath(metaDir, s"${meta.id}.json")

    writeFile(meta.toJson, metaPath)

  def copyFile(orig: Path, dest: Path): AppM[Unit] =
    safeIO(err => IOError(s"can not copy file: $err")) {
      Files.copy(orig, dest, StandardCopyOption.REPLACE_EXISTING)
    }

  extension (meta: Paper) {
    def writeTo(libDir: String): AppM[Unit] = writeMeta(libDir, meta)
  }

  extension (f: File) {
    def safeCheckExists: AppM[Boolean] = checkFileExists(f)
    def ensureExists: AppM[File] = ensureFileExists(f)
  }
}
