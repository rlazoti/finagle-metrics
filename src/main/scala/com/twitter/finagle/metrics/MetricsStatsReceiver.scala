package com.twitter.finagle.metrics

import com.codahale.metrics.{Metric, MetricFilter, MetricRegistry, Gauge => MGauge}
import com.twitter.finagle.stats.{Counter, CounterSchema, Gauge, GaugeSchema, HistogramSchema, Stat, StatsReceiver, Verbosity}

import scala.collection.JavaConverters._

object MetricsStatsReceiver {
  val metrics: MetricRegistry = new MetricRegistry

  private[metrics] case class MetricCounter(name: String) extends Counter {
    private val meter = metrics.meter(name)
    override def incr(delta: Long): Unit = meter.mark(delta)
  }

  private[metrics] case class MetricGauge(name: String)(f: => Float) extends Gauge {
    // we need to synchronize so this is safe with multiple threads. Ideally the callers are themselves
    // synchronized so they don't overwrite each others gauges but until they are we should protect
    // ourselves from that race condition.
    metrics.synchronized {
      // remove old gauge's value before adding a new one
      metrics.getGauges(new MetricFilter() {
        override def matches(metricName: String, metric: Metric): Boolean =
          metricName == name
      }).asScala
        .foreach { case (gaugeName, _) => metrics.remove(gaugeName) }

      metrics.register(name, new MGauge[Float]() {
        override def getValue(): Float = f
      })
    }

    override def remove(): Unit = metrics.remove(name)
  }

  private[metrics] case class MetricStat(name: String) extends Stat {
    private val histogram = metrics.histogram(name)
    override def add(value: Float): Unit = histogram.update(value.toLong)
  }
}

class MetricsStatsReceiver extends StatsReceiver {
  override val repr: AnyRef = this

  private[this] def format(names: Seq[String]) =
    names.mkString(".")

  override def counter(verbosity: Verbosity, names: String*): Counter =
    MetricsStatsReceiver.MetricCounter(format(names))

  override def addGauge(verbosity: Verbosity, names: String*)(f: => Float): Gauge =
    MetricsStatsReceiver.MetricGauge(format(names)) (f)

  override def stat(verbosity: Verbosity, names: String*): Stat =
    MetricsStatsReceiver.MetricStat(format(names))

  override def counter(schema: CounterSchema): Counter =
    MetricsStatsReceiver.MetricCounter(format(schema.metricBuilder.name))

  override def stat(schema: HistogramSchema): Stat =
    MetricsStatsReceiver.MetricStat(format(schema.metricBuilder.name))

  override def addGauge(schema: GaugeSchema)(f: => Float): Gauge =
    MetricsStatsReceiver.MetricGauge(format(schema.metricBuilder.name))(f)
}
