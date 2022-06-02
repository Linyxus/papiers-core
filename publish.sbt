ThisBuild / organization := "io.github.linyxus"
ThisBuild / organizationName := "linyxus"
ThisBuild / organizationHomepage := Some(url("https://linyxus.github.io"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/Linyxus/papiers-core"),
    "scm:git@github.com:Linyxus/papiers-core.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id    = "linyxus",
    name  = "Yichen Xu",
    email = "yichen.x@outlook.com",
    url   = url("https://linyxus.github.io")
  )
)

ThisBuild / description := "A reference and document manager for researchers."
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/Linyxus/papiers-core"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val nexus = "https://s01.oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true
