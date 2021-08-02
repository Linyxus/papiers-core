package papiers.io

import org.apache.pdfbox.pdmodel.PDDocument
import java.io.File

import papiers.core._
import MonadApp._


trait Pdf {
  def getPdfTitle(pdf: File): AppM[String] =
    def errF(err: Throwable): IOError = IOError(s"can not retrieve title of ${pdf.toPath.toString}: $err")

    safeIO(errF) {
      val doc = PDDocument.load(pdf)
      val info = doc.getDocumentInformation()

      info.getTitle
    }
}

object Pdf extends Pdf
