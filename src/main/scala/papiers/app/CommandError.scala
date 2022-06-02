package papiers.app

import papiers.core.AppError

case class CommandError(desc: String) extends AppError
