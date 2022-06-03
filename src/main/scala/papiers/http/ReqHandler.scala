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

object ReqHandler extends PaperInstances with AppHandler {
  import AppResp.PaperList

  def listPapers: AppM[PaperList] =
    loadLibrary map { lib => lib.map { (_, v) => v.paper } } map { x => x.toList } map PaperList.apply
}

