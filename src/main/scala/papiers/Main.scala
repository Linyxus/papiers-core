package papiers

import cats.effect._
import cats.implicits._

import com.monovore.decline._
import com.monovore.decline.effect._

import cli.CLIParser.commandParser
import cli.CLIApp.handleCommand

object PapiersApp extends CommandIOApp(
  name = "pac",
  header = "Papiers Core Command Line Interface",
  version = "0.0.0"
) {
  override def main: Opts[IO[ExitCode]] =
    commandParser map {
      case c => handleCommand(c)
    }
}
