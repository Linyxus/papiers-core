package papiers.core


case class PaperFilter(filter: Paper => Boolean):
  def * (other: PaperFilter): PaperFilter = PaperFilter { p =>
    filter(p) && other(p)
  }

  def + (other: PaperFilter): PaperFilter = PaperFilter { p =>
    filter(p) || other(p)
  }

  def apply(p: Paper): Boolean = filter(p)

object PaperFilter:
  def titleFilter(word: String): PaperFilter = PaperFilter { p =>
    val words = p.title.split("\\s+").toList
    words exists (_.toLowerCase.startsWith(word.toLowerCase))
  }

  def identityFilter: PaperFilter = PaperFilter { p => true }

  def zeroFilter: PaperFilter = PaperFilter { p => false }

  def product(filters: List[PaperFilter]): PaperFilter =
    filters.foldRight(identityFilter) { _ * _ }

  def sum(filters: List[PaperFilter]): PaperFilter =
    filters.foldRight(zeroFilter) { _ + _ }
