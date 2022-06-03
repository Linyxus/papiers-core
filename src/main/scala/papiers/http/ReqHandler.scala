package papiers.http

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

/** Handler for Http requests */
object ReqHandler extends PaperInstances with AppHandler {
  import AppResp._

  /** List all papers. */
  def listPapers: AppM[PaperList] =
    loadLibrary map { lib => lib.map { (_, v) => v.paper } } map { x => x.toList } map PaperList.apply

  /** Retrieve paper information with given operator. */
  def getInfoWith[X](f: PaperBundle => X)(paperId: Int): AppM[X] =
    loadLibrary flatMap { lib =>
      lib.get(paperId) match {
        case None => MonadApp.throwError(CommandError(s"Paper id non-exist: $paperId"))
        case Some(bundle) => MonadApp.pure(f(bundle))
      }
    }

  def getSummary: Int => AppM[PaperSummary] =
    getInfoWith { (bundle: PaperBundle) => PaperSummary(bundle.paper.toString) }

  def getBib: Int => AppM[PaperBib] =
    getInfoWith { (bundle: PaperBundle) => PaperBib(bundle.paper.toBib) }

  def getPdf: Int => AppM[PaperPdf] =
    getInfoWith { (bundle: PaperBundle) => PaperPdf(bundle.pdf.toPath.toAbsolutePath.toString) }
}

