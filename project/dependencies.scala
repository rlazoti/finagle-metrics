import sbt._

object Dependencies {

  val finagleVersion         = "6.35.0"
  val metricsVersion         = "3.1.0"

  val finagleCore            = "com.twitter"           %% "finagle-core"     % finagleVersion
  val metricsCore            = "io.dropwizard.metrics" %  "metrics-core"     % metricsVersion
  val scalatest              = "org.scalatest"         %% "scalatest"        % "2.2.6"
  val junit                  = "junit"                 %  "junit"            % "4.11"

  val appDependencies        = Seq(
    metricsCore,
    finagleCore % "provided",
    scalatest % "test",
    junit % "test"
  )

  val appDependencyResolvers = Seq(
    "twttr" at "http://maven.twttr.com/"
  )

}
