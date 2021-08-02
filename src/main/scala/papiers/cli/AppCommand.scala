package papiers.cli

enum AppCommand {
  case ListPapers(collection: Option[String])
  case GetPaperInfo(paper: Int, getPdf: Boolean, getBib: Boolean, getSummary: Boolean)
  case Serve(bind: String, port: String)
}

