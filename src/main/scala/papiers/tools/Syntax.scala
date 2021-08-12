package papiers.tools
import cats.Monad
import org.json4s._

trait Syntax {
  extension [F[_], X] (mxs: List[F[X]])(using F: Monad[F]) {
    def chainM: F[List[X]] = Monads.chainM(mxs)
  }

  extension [F[_], A, B] (mab: (F[A], F[B]))(using F: Monad[F]) {
    def tupleM: F[(A, B)] = Monads.tupleM(mab._1, mab._2)
  }

  extension (x: JValue)
    def select(p: List[String]): Option[JValue] = JsonValue.select(x, p)

    def selectAsString(p: List[String]): Option[String] = x.select(p) flatMap {
      case JString(s) => Some(s)
      case _ => None
    }

    def selectAsList(p: List[String]): Option[List[JValue]] = x.select(p) flatMap {
      case JArray(arr) => Some(arr)
      case _ => None
    }
}

object Syntax extends Syntax