package com.twitter.finagle.metrics

import com.codahale.metrics.{Gauge => JGauge}
import com.twitter.finagle.stats.{Counter, Gauge, Stat, StatsReceiver}
import com.codahale.metrics.{MetricRegistry, MetricFilter}
import java.net.{InetAddress, InetSocketAddress, UnknownHostException}
import java.util.concurrent.TimeUnit;

object MetricsStatsReceiver {
  val metrics: MetricRegistry = new MetricRegistry
}

class MetricsStatsReceiver extends StatsReceiver {
  import MetricsStatsReceiver._

  override val repr: AnyRef = this

  private[this] def format(names: Seq[String]) =
    names.mkString(".")

  override def counter(names: String*): Counter =
    MetricCounter(format(names))

  override def addGauge(names: String*)(f: => Float): Gauge =
    MetricGauge(format(names)) (f)

  override def stat(names: String*): Stat =
    MetricStat(format(names))
}

case class MetricCounter(name: String) extends Counter {
  import MetricsStatsReceiver._

  private val meter = metrics.meter(name)
  override def incr(delta: Int): Unit = meter.mark(delta)
}

case class MetricGauge(name: String)(f: => Float) extends Gauge {
  import MetricsStatsReceiver._

  metrics.register(name, new JGauge[Float]() {
    override def getValue(): Float = f
  })

  override def remove(): Unit = Unit
}

case class MetricStat(name: String) extends Stat {
  import MetricsStatsReceiver._

  private val histogram = metrics.histogram(name)
  override def add(value: Float): Unit = histogram.update(value.toLong)
}
