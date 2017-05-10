import com.codahale.metrics.ConsoleReporter
import com.twitter.finagle.{ Http, Service }
import com.twitter.finagle.metrics.MetricsStatsReceiver
import com.twitter.finagle.http.{ Request, Response, Status }
import com.twitter.io.Charsets
import com.twitter.server.TwitterServer
import com.twitter.util.{ Await, Future }
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
