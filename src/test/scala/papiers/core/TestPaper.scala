package papiers
package core

import org.junit.Test
import org.junit.Assert.*

class TestPaper:
  @Test def testToString: Unit =
    val p1 = Paper(0, "test", authors = List(AuthorName("Xu", "Yichen")), venue = Some("arXiv"), year = Some("2019"), pages = None)
    assertEquals(p1.toString, "test, Xu et al., arXiv (2019)")

    val p2 = Paper(0, "test", authors = Nil, venue = Some("arXiv"), year = Some("2019"), pages = None)
    assertEquals(p2.toString, "test, unknown, arXiv (2019)")

    val p3 = Paper(0, "test", authors = Nil, venue = Some("arXiv"), year = None, pages = None)
    assertEquals(p3.toString, "test, unknown, arXiv")

  @Test def testJsonCodec: Unit = {
    val testcases = Seq(
      Paper(0, "test", authors = List(AuthorName("Xu", "Yichen")), venue = Some("arXiv"), year = Some("2019"), pages = None)
        -> """{"id":0,"title":"test","authors":[{"surname":"Xu","givenName":"Yichen"}],"venue":"arXiv","year":"2019","pages":null}""",
    )
    // encoding tests
    testcases foreach { (paper, expected) => assertEquals(paper.toJson, expected) }

    // decoding tests
    testcases foreach { (expected, input) => assertEquals(Paper.fromJson(input).getOrElse(null), expected) }
  }

