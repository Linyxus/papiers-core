package papiers.cli

import java.nio.file.Path

enum AppCommand {
  case ListPapers(collection: Option[String])
  case GetPaperInfo(paper: Int, getPdf: Boolean, getBib: Boolean, getSummary: Boolean)
  case ImportPaper(pdfPath: Path)
  case Serve(bind: String, port: String)
}

