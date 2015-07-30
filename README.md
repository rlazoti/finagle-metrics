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

Easy way to send Finagle metrics to Codahale Metrics library.

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

If you're using [twitter-server](https://github.com/twitter/twitter-server) to create your finagle services, everything you need to do is just adding the finagle-metrics jar to your project.

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
import java.util.concurrent.TimeUnit;

val graphite = new Graphite(new InetSocketAddress("graphite.example.com", 2003));
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
