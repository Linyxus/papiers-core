val scala3Version = "3.3.0-RC3"
val http4sVersion = "1.0.0-M29"
val munitVersion = "0.7.29"
val logbackVersion = "1.2.6"
val munitCatsEffectVersion = "1.0.6"


lazy val root = project
  .in(file("."))
  .settings(
    name := "papiers-core",
    version := "0.2.0",

    scalaVersion := scala3Version,

    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"
  )

val catsDeps = Seq(
  "org.typelevel" %% "cats-core" % "2.6.1",
  "org.typelevel" %% "cats-effect" % "3.1.1",
)

val declineDeps = Seq(
  "com.monovore" %% "decline" % "2.1.0",
  "com.monovore" %% "decline-effect" % "2.1.0",
)

val circeDep = Seq(
  "io.circe" %% "circe-core" % "0.15.0-M1",
  "io.circe" %% "circe-generic" % "0.15.0-M1",
  "io.circe" %% "circe-parser" % "0.15.0-M1",
)

val pdfBoxDep = Seq(
  "org.apache.pdfbox" % "pdfbox" % "2.0.24",
)

val sttpDep = Seq(
  "com.softwaremill.sttp.client3" %% "core" % "3.3.13",
  "com.softwaremill.sttp.client3" %% "async-http-client-backend-cats" % "3.3.13"
)

val json4sDep = Seq(
  "org.json4s" %% "json4s-native" % "4.0.3"
)

val http4sDep = Seq(
  "org.http4s"      %% "http4s-ember-server" % http4sVersion,
  "org.http4s"      %% "http4s-ember-client" % http4sVersion,
  "org.http4s"      %% "http4s-circe"        % http4sVersion,
  "org.http4s"      %% "http4s-dsl"          % http4sVersion,
  "org.scalameta"   %% "munit"               % munitVersion           % Test,
  "org.typelevel"   %% "munit-cats-effect-3" % munitCatsEffectVersion % Test,
  "ch.qos.logback"  %  "logback-classic"     % logbackVersion,
)


libraryDependencies ++= catsDeps
libraryDependencies ++= declineDeps
libraryDependencies ++= circeDep
libraryDependencies ++= pdfBoxDep
libraryDependencies ++= sttpDep
libraryDependencies ++= json4sDep
libraryDependencies ++= http4sDep
