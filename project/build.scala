import sbt._
import sbt.Keys._
import Dependencies._

object AppBuilder extends Build {

  val appName     = "finagle-metrics"
  val appSettings = Seq(
    name          := appName,
    organization  := "com.github.rlazoti",
    version       := "0.0.1-SNAPSHOT",
    scalaVersion  := "2.11.4",
    unmanagedSourceDirectories in Compile <<= (scalaSource in Compile)(Seq(_)),
    unmanagedSourceDirectories in Test    <<= (scalaSource in Test)(Seq(_))
  )

  lazy val app = Project(appName, file("."))
    .settings(appSettings: _*)
    .settings(resolvers ++= appDependencyResolvers)
    .settings(libraryDependencies ++= appDependencies)

}
