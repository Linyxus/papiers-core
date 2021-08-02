package papiers.tools
import cats.Monad

trait Syntax {
  extension [F[_], X] (mxs: List[F[X]])(using F: Monad[F]) {
    def chainM: F[List[X]] = Monads.chainM(mxs)
  }

  extension [F[_], A, B] (mab: (F[A], F[B]))(using F: Monad[F]) {
    def tupleM: F[(A, B)] = Monads.tupleM(mab._1, mab._2)
  }
}

object Syntax extends Syntax