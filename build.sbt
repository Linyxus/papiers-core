val scala3Version = "3.1.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "papiers-core",
    version := "0.1.0",

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

libraryDependencies ++= catsDeps
libraryDependencies ++= declineDeps
libraryDependencies ++= circeDep
libraryDependencies ++= pdfBoxDep
libraryDependencies ++= sttpDep
libraryDependencies ++= json4sDep
