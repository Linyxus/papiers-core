package papiers.core

import util.Try

import cats.data.EitherT
import cats.implicits._
import cats.effect._

/** Monads and operators for Papier application. */
object MonadApp {
  /** Application monad. */
  type AppM = [X] =>> EitherT[IO, AppError, X]

  /** Throw an error. */
  def throwError[X](err: AppError): AppM[X] = EitherT.fromEither(Left(err))

  def pure[X](x: X): AppM[X] = EitherT.pure(x)

  /** Handle errors from the app purely. */
  def mapError[X](app: AppM[X])(handler: AppError => X): AppM[X] = EitherT {
    val computation = app.value map {
      case Left(err) => Right(handler(err))
      case Right(x) => Right(x)
    }
    computation
  }

  /** Handle application error with effects. */
  def handleError[X](app: AppM[X])(handler: AppError => AppM[X]): AppM[X] = EitherT {
    val computation: IO[Either[AppError, X]] = app.value flatMap {
      case Left(err) => handler(err).value
      case Right(x) => IO.pure(Right(x))
    }
    computation
  }

  def liftIO[X](action: IO[X]) = EitherT.liftF(action)

  def safeIO_[X](operation: => X): AppM[X] = EitherT {
    IO {
      Try {
        val res = operation
        res
      }.toEither match {
        case Left(err) => Left(UnknownError(err.toString))
        case Right(res) => Right(res)
      }
    }
  }

  def safeIO[X](errorBuilder: Throwable => AppError)(operation: => X): AppM[X] = EitherT {
    IO {
      Try {
        val res = operation
        res
      }.toEither match {
        case Left(err) => Left(errorBuilder(err))
        case Right(res) => Right(res)
      }
    }
  }

  def safely[X](func: => X): Option[X] = Try(func).toOption

  def executeApp(app: AppM[Unit]): IO[ExitCode] = app.value flatMap {
    case Left(error) =>
      IO.println(error.toString) as ExitCode.Error
    case Right(_) => IO.pure { ExitCode.Success }
  }

  extension[X] (app: AppM[X]) {
    def errorPureAction(handler: AppError => X): AppM[X] = mapError(app)(handler)
    def errorAction(handler: AppError => AppM[X]): AppM[X] = handleError(app)(handler)
    def execute: IO[ExitCode] = executeApp(app as ())
  }
}
