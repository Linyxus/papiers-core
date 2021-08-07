package papiers.core

import scala.sys.Prop

trait PropSetter:
  type V

  def setProp(paper: Paper, s: String)(using p: StrParser[V]): Option[Paper] =
    p.parse(s) map { v => set(paper, v) }

  protected def set(paper: Paper, v: V): Paper

object PropSetter:
  import StrParser.{_, given}

  val titleSetter = new PropSetter {
    type V = String

    override def set(paper: Paper, title: String): Paper =
      paper.copy(title = title)
  }

  val allSetters: Map[String, PropSetter] = Map(
    "title" -> titleSetter
  )
