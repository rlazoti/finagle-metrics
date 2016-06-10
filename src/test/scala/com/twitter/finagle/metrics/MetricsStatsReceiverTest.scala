package com.twitter.finagle.metrics

import com.twitter.finagle.metrics.MetricsStatsReceiver._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite

@RunWith(classOf[JUnitRunner])
class MetricsStatsReceiverTest extends FunSuite {

  private[this] val receiver = new MetricsStatsReceiver()

  private[this] def readGauge(name: String): Option[Number] =
    Option(metrics.getGauges.get(name)) match {
      case Some(gauge) => Some(gauge.getValue.asInstanceOf[Float])
      case _ => None
    }

  private[this] def readCounter(name: String): Option[Number] =
    Option(metrics.getMeters.get(name)) match {
      case Some(counter) => Some(counter.getCount)
      case _ => None
    }

  private[this] def readStat(name: String): Option[Number] =
    Option(metrics.getHistograms.get(name)) match {
      case Some(stat) => Some(stat.getSnapshot.getValues.toSeq.sum)
      case _ => None
    }

  test("MetricsStatsReceiver should store and read gauge into the Codahale Metrics library") {
    val x = 1.5f
    receiver.addGauge("my_gauge")(x)

    assert(readGauge("my_gauge") === Some(x))
  }

  test("MetricsStatsReceiver should always assume the latest value of an already created gauge") {
    val gaugeName = "my_gauge2"
    val expectedValue = 8.8f

    receiver.addGauge(gaugeName)(2.2f)
    receiver.addGauge(gaugeName)(9.9f)
    receiver.addGauge(gaugeName)(expectedValue)

    assert(readGauge(gaugeName) === Some(expectedValue))
  }

  test("MetricsStatsReceiver should store and remove gauge into the Codahale Metrics Library") {
    val gaugeName = "temp-gauge"
    val expectedValue = 2.8f

    val tempGauge = receiver.addGauge(gaugeName)(expectedValue)
    assert(readGauge(gaugeName) === Some(expectedValue))

    tempGauge.remove()

    assert(readGauge(gaugeName) === None)
  }

  test("MetricsStatsReceiver should store and read stat into the Codahale Metrics library") {
    val x = 1
    val y = 3
    val z = 5

    val s = receiver.stat("my_stat")
    s.add(x)
    s.add(y)
    s.add(z)

    assert(readStat("my_stat") === Some(x + y + z))
  }

  test("MetricsStatsReceiver should store and read counter into the Codahale Metrics library") {
    val x = 2
    val y = 5
    val z = 8

    val c = receiver.counter("my_counter")
    c.incr(x)
    c.incr(y)
    c.incr(z)

    assert(readCounter("my_counter") === Some(x + y + z))
  }

}
