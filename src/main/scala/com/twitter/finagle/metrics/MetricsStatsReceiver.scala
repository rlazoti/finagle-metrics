package com.twitter.finagle.metrics

import com.codahale.metrics.{Gauge => MGauge, MetricRegistry}
import com.twitter.finagle.stats.{Counter, Gauge, Stat, StatsReceiver}

object MetricsStatsReceiver {
  val metrics: MetricRegistry = new MetricRegistry

  private[metrics] case class MetricCounter(name: String) extends Counter {
    private val meter = metrics.meter(name)
    override def incr(delta: Int): Unit = meter.mark(delta)
  }

  private[metrics] case class MetricGauge(name: String)(f: => Float) extends Gauge {
    metrics.register(name, new MGauge[Float]() {
      override def getValue(): Float = f
    })

    override def remove(): Unit = metrics.remove(name)
  }

  private[metrics] case class MetricStat(name: String) extends Stat {
    private val histogram = metrics.histogram(name)
    override def add(value: Float): Unit = histogram.update(value.toLong)
  }
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
