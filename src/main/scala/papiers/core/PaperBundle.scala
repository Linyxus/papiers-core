package papiers.core

import java.io.File

/** A PaperBundle is a paper's metadata combined with its pdf file. */
case class PaperBundle(paper: Paper, pdf: File)

