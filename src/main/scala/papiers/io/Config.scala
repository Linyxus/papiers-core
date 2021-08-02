package papiers.io

import papiers.core.MonadApp._
import java.lang.System


object Config {
  def getUserHome: AppM[String] = 
    def errF(err: Throwable) = IOError(s"can not read user home from system: $err")
    safeIO(errF) {
      System.getProperty("user.home")
    }

  def getLibraryDir: AppM[String] =
    getUserHome map { base => Tools.joinPath(base, ".papiers") }
}