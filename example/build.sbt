name := "finagle-metrics-example"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.12.1"

resolvers += "twttr" at "https://maven.twttr.com/"
resolvers += Resolver.sonatypeRepo("public")

libraryDependencies ++= Seq(
  "com.twitter"        %% "twitter-server"  % "20.3.0",
  "com.github.rlazoti" %% "finagle-metrics" % "0.0.13"
)
