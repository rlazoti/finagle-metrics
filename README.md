[![][travis img]][travis]
[![][release img]][release]
[![][license img]][license]

[travis]:https://travis-ci.org/rlazoti/finagle-metrics
[travis img]:https://travis-ci.org/rlazoti/finagle-metrics.svg?branch=master

[release]:https://github.com/rlazoti/finagle-metrics/releases
[release img]:https://img.shields.io/github/release/rlazoti/finagle-metrics.svg

[license]:LICENSE
[license img]:https://img.shields.io/dub/l/vibe-d.svg


finagle-metrics
===============

Easy way to send [Finagle](https://github.com/twitter/finagle) metrics to Codahale Metrics library.

## Overview

*finagle-metrics* enables your finagle based application to send its metrics to [Codahale Metrics library](https://github.com/dropwizard/metrics) instead of the default metrics ([finagle-stats](https://github.com/twitter/finagle/tree/master/finagle-stats)).

### Build

```sh
$ git clone https://github.com/rlazoti/finagle-metrics.git
$ cd finagle-metrics
$ sbt package
```

### Test

```sh
$ sbt test
```

### Setup

Finagle-metrics is available on [OSS Sonatype](https://oss.sonatype.org).

So everything you need to do is add the sbt dependency like:


```scala
"com.github.rlazoti" %% "finagle-metrics" % "0.0.2"
```

or

```scala
"com.github.rlazoti" % "finagle-metrics_2.11" % "0.0.2"
```

### Usage

#### Using twitter-server

If you're using [twitter-server](https://github.com/twitter/twitter-server) to create your finagle services, everything you need to do is just adding the finagle-metrics dependency to your project.

You don't need to add the finagle-stats to your project, but both libraries will work together without any issue if you need it.

#### Including your own metrics

You can include your own metrics through the **statsReceiver** field of TwitterServer, so your metrics will be sent to Codahale Metrics as well.

#### Including your own metrics through Codahale Metrics

You can obtain an instance of MetricRegistry class through the field **metrics** of MetricsStatsReceiver.

```scala
import com.twitter.finagle.metrics.MetricsStatsReceiver

val myCustomMeter = MetricsStatsReceiver.metrics.meter("my-custom-meter")
myCustomMeter.mark()
```

#### Reporting

Codahale Metrics library has [reporters](https://dropwizard.github.io/metrics/3.1.0/getting-started/#other-reporting) for many diferent outputs.

Let's take the GraphiteReporter as example.

```scala
import com.codahale.metrics.graphite.{Graphite, GraphiteReporter}
import com.twitter.finagle.metrics.MetricsStatsReceiver
import java.util.concurrent.TimeUnit

val graphite = new Graphite(new InetSocketAddress("graphite.example.com", 2003))
val reporter = GraphiteReporter.forRegistry(MetricsStatsReceiver.metrics)
                               .prefixedWith("finagle-service.example.com")
                               .convertRatesTo(TimeUnit.SECONDS)
                               .convertDurationsTo(TimeUnit.MILLISECONDS)
                               .build(graphite)

reporter.start(1, TimeUnit.MINUTES)
```

And an example using JmxReporter.

```scala
import com.codahale.metrics.JmxReporter
import com.twitter.finagle.metrics.MetricsStatsReceiver

val reporter: JmxReporter = JmxReporter.forRegistry(MetricsStatsReceiver.metrics)
                                       .build()

reporter.start()
```

#### Full Example

Let's create a full example that reports its metrics to console each five seconds.

Firstly, let's create the build.sbt:

```scala
name := "finagle-metrics-example"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.twitter"        %% "twitter-server"  % "1.20.0",
  "com.github.rlazoti" %% "finagle-metrics" % "0.0.2"
)
```

Then the App.scala:

```scala
import com.codahale.metrics.ConsoleReporter
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.metrics.MetricsStatsReceiver
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.io.Charsets
import com.twitter.server.TwitterServer
import com.twitter.util.{Await, Future}
import java.util.concurrent.TimeUnit

object App extends TwitterServer {

  val service = new Service[Request, Response] {
    def apply(request: Request) = {
      val response = Response(request.version, Status.Ok)
      response.contentString = "hello"
      Future.value(response)
    }
  }

  val reporter = ConsoleReporter
    .forRegistry(MetricsStatsReceiver.metrics)
    .convertRatesTo(TimeUnit.SECONDS)
    .convertDurationsTo(TimeUnit.MILLISECONDS)
    .build

  def main() = {
    val server = Http.serve(":8080", service)
    reporter.start(5, TimeUnit.SECONDS)

    onExit { server.close() }

    Await.ready(server)
  }

}
```

That's all Folks! :)


Author
======

Rodrigo Lazoti - rodrigolazoti@gmail.com
