package papiers.io

import scala.language.postfixOps

import sys.process._
import java.net.URL
import java.io.File

import papiers.core._
import MonadApp._

object Download:
  def downloadFile(url: String, savePath: String): AppM[Unit] =
    def errF(err: Throwable): IOError = IOError(s"can not download from $url to $savePath: $err")

    safeIO(errF) {
      new URL(url) #> new File(savePath) !!
    }
