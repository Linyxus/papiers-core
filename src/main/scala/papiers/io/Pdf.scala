package papiers.io

import scala.jdk.CollectionConverters._

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File

import papiers.core._
import MonadApp._


trait Pdf {
  def getPdfTitle(pdf: File): AppM[String] =
    def errF(err: Throwable): IOError = IOError(s"can not retrieve title of ${pdf.toPath.toString}: $err")

    def extractTitle: AppM[String] =
      safeIO(errF) {
        val doc = PDDocument.load(pdf)
        val info = doc.getDocumentInformation()
        val title = info.getTitle()
        doc.close()
        title
      }

    def extractTitleByText: AppM[String] =
      safeIO(errF) {
        val doc = PDDocument.load(pdf)
        val stripper = new PDFTextStripper
        val text: String = stripper.getText(doc)
        doc.close()

        text.linesIterator.toList.headOption getOrElse "unknown"
      }

    extractTitle.flatMap { res =>
      if res.ne(null) && res.nonEmpty then MonadApp.pure(res)
      else extractTitleByText
    }
}

object Pdf extends Pdf
