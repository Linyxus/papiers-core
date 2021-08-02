package papiers.core

trait AppError

case class UnknownError(desc: String) extends AppError

case class JsonDecodeError(desc: String) extends AppError
