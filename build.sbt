import Dependencies._
import ReleaseTransformations._

lazy val root = (project in file("."))
val appName   = "finagle-metrics"

name                      := appName
organization              := "com.github.rlazoti"
scalaVersion in ThisBuild := "2.12.1"
crossScalaVersions        := Seq("2.11.8", "2.12.1")
releaseTagName            := s"v${(version in ThisBuild).value}"
releaseTagComment         := s"[BUILD] Release ${(version in ThisBuild).value}"
releaseCommitMessage      := s"[BUILD] Set version to ${(version in ThisBuild).value}"

scalacOptions := Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:postfixOps",
  "-target:jvm-1.8")

javacOptions in compile ++= Seq(
  "-target", "8",
  "-source", "8")

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("publishSigned", _)),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _)))

resolvers           ++= appDependencyResolvers
libraryDependencies ++= appDependencies
