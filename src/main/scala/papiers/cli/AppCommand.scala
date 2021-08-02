package papiers.cli

enum AppCommand {
  case ListPapers(collection: Option[String])
  case GetPaperBib(paper: String)
  case GetPaperPdf(paper: String)
  case Serve(bind: String, port: String)
}

