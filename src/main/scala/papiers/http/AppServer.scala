package papiers.http

import cats.effect.IO
import cats.implicits._
import org.http4s.implicits._
import org.http4s.ember.client.EmberClientBuilder
import com.comcast.ip4s._
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.Logger

object AppServer {
  def run(port: Port): IO[Nothing] = {
    def service = {
          val httpApp = (AppRoutes.testRoutes <+> AppRoutes.coreRoutes).orNotFound
          val finalHttpApp = Logger.httpApp(true, true)(httpApp)
          EmberServerBuilder.default[IO]
            .withHost(ipv4"0.0.0.0")
            .withPort(port)
            .withHttpApp(finalHttpApp)
            .build
        }
      service.useForever
  }
}

