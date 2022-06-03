package papiers.cli

import com.comcast.ip4s._

enum CliCommand:
  case RunDaemon(port: Port)
