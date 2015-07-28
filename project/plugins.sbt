resolvers += Resolver.sonatypeRepo("snapshots")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.5")

addSbtPlugin("org.ensime" % "ensime-sbt" % "0.1.5-SNAPSHOT")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.5.0")
