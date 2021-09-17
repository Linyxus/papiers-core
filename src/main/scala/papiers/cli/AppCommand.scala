package papiers.cli

import java.nio.file.Path

enum AppCommand:
  case ListPapers(query: List[String], collection: Option[String])
  case GetPaperInfo(paper: Int, getPdf: Boolean, getBib: Boolean, getSummary: Boolean)
  case ImportPaper(pdfPath: Path)
  case DownloadPaper(url: String, arxiv: Boolean)
  case SetProp(paper: Int, key: String, value: String)
  case MatchPaper(paper: Int)
  case Serve(bind: String, port: String)
