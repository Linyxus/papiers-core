package papiers.io

import papiers.core.AppError

case class IOError(desc: String) extends AppError
