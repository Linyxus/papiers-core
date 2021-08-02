package papiers.cli

enum AppCommand {
  case ListPapers(collection: Option[String])
  case GetPaperBib(paper: String)
  case GetPaperPdf(paper: String)
  case GetPaperInfo(paper: Int)
  case Serve(bind: String, port: String)
}

