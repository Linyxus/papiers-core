package papiers

import cats.effect._
import cats.implicits._

import com.monovore.decline._
import com.monovore.decline.effect._

import cli.CLIParser.commandParser
import core.MonadApp._
import io.Library._
import io.Tools

object PapiersApp extends CommandIOApp(
  name = "pac",
  header = "Papiers Core Command Line Interface",
  version = "0.0.0"
) {
  override def main: Opts[IO[ExitCode]] =
    commandParser map {
      case c =>
        for
          _ <- IO.println("Hello, world")
          code <- initLibrary("/Users/linyxus/.papiers/").execute
          allFiles <- Tools.listFiles("/Users/linyxus/.papiers/pdf").value
          pdfs <- Tools.listPdfs("/Users/linyxus/.papiers/pdf").value
          metas <- Tools.listMetas("/Users/linyxus/.papiers/meta").value
          _ <- IO.println(allFiles map { xs => xs map (_.getName) })
          _ <- IO.println(pdfs)
          _ <- IO.println(metas)
          lib <- loadLibrary("/Users/linyxus/.papiers/").value
          _ <- IO.println(lib)
        yield
          code
    }
}
