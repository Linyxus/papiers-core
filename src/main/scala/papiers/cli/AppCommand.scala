package papiers.cli

import java.nio.file.Path

enum AppCommand {
  case ListPapers(collection: Option[String])
  case GetPaperInfo(paper: Int, getPdf: Boolean, getBib: Boolean, getSummary: Boolean)
  case ImportPaper(pdfPath: Path)
  case SetProp(paper: Int, key: String, value: String)
  case Serve(bind: String, port: String)
}

