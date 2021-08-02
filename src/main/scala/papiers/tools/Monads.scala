package papiers.tools

import cats.Monad
import cats.implicits._

trait Monads {
  def chainM[F[_], X](mxs: List[F[X]])(using F: Monad[F]): F[List[X]] = {
    def recur(mxs: List[F[X]], cont: F[List[X]] => F[List[X]]): F[List[X]] = mxs match {
      case Nil => cont(F.pure(Nil))
      case x :: xs => recur(xs, { m => cont(F.flatMap(x)(i => F.map(m)(is => i :: is))) })
    }
    recur(mxs, x => x)
  }

  def tupleM[F[_], A, B](ma: F[A], mb: F[B])(using F: Monad[F]): F[(A, B)] =
    F.flatMap(ma) { a => F.map(mb) { b => (a, b) } }
}

object Monads extends Monads
