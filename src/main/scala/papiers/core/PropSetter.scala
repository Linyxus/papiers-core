package papiers.core

import scala.sys.Prop

trait PropSetter:
  type V

  val p: StrParser[V]

  def setProp(paper: Paper, s: String): Option[Paper] =
    p.parse(s) map { v => set(paper, v) }

  protected def set(paper: Paper, v: V): Paper

object PropSetter:
  import StrParser.{_, given}

  val titleSetter = new PropSetter {
    type V = String

    val p: StrParser[V] = implicitly

    override def set(paper: Paper, title: String): Paper =
      paper.copy(title = title)
  }

  val authorSetter = new PropSetter {
    type V = List[AuthorName]

    val p: StrParser[V] = implicitly

    override def set(paper: Paper, authors: List[AuthorName]): Paper =
      paper.copy(authors = authors)
  }

  val allSetters: Map[String, PropSetter] = Map(
    "title" -> titleSetter,
    "authors" -> authorSetter
  )
