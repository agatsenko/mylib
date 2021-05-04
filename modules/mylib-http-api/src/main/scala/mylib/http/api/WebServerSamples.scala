/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2019-12-16
 */
package mylib.http.api

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import scala.util.Random

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.Source
import akka.util.ByteString

// FIXME: need to remove
object WebServerSamples {
  val PORT = 9000

  def main(args: Array[String]): Unit = {
    routeSample()
    lowLevelSample()
    streamSample()
  }

  def routeSample(): Unit = {
    println("Start route sample ...")

    implicit val system: ActorSystem = ActorSystem("route-sample")
    implicit val materializer: Materializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val route = concat(
      pathEndOrSingleSlash {
        get {
          complete(
            HttpEntity(
              ContentTypes.`text/html(UTF-8)`,
              s"""
                 |<h1>Route Sample</h1>
                 |<a href="http://localhost:$PORT/hello">Hello</a>
                 |""".stripMargin
            )
          )
        }
      },
      path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Hello, Stranger!</h1>"))
        }
      },
    )

    val bindingFuture = Http().bindAndHandle(route, "localhost", PORT)

    println(s"Server online at http://localhost:$PORT\nPress RETURN to continue...")
    StdIn.readLine()
    bindingFuture
        .flatMap(_.unbind())
        .onComplete(_ => system.terminate())
    println("End route sample")
    println()
  }

  def lowLevelSample(): Unit = {
    import akka.http.scaladsl.model.HttpMethods._

    println("Start low-level sample ...")

    implicit val system: ActorSystem = ActorSystem("low-level-sample")
    implicit val materializer: Materializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val reqHandler: HttpRequest => HttpResponse = {
      case HttpRequest(GET, Uri.Path("/"), _, _, _) => HttpResponse(
        entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          s"""
             |<h1>Low-Level Sample</h1>
             |<a href="http://localhost:$PORT/hello">Hello</a>
             |""".stripMargin
        )
      )

      case HttpRequest(GET, Uri.Path("/hello"), _, _, _) => HttpResponse(
        entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Hello, Stranger!</h1>")
      )

      case r: HttpRequest =>
        r.discardEntityBytes() // important to drain incoming HTTP Entity stream
        HttpResponse(404, entity = "not found")
    }

    val bindingFuture = Http().bindAndHandleSync(reqHandler, "localhost", PORT)

    println(s"Server online at http://localhost:$PORT\nPress RETURN to continue...")
    StdIn.readLine()
    bindingFuture
        .flatMap(_.unbind())
        .onComplete(_ => system.terminate())

    println("End low-level sample")
    println()
  }

  def streamSample(): Unit = {
    println("Start stream sample ...")

    implicit val system: ActorSystem = ActorSystem("stream-sample")
    implicit val materializer: Materializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val numbers = Source.fromIterator(() => Iterator.continually(Random.nextInt()))

    val route = concat(
      pathEndOrSingleSlash {
        get {
          complete(
            HttpEntity(
              ContentTypes.`text/html(UTF-8)`,
              s"""
                 |<h1>Stream Sample</h1>
                 |<a href="http://localhost:$PORT/random">Random Numbers</a>
                 |""".stripMargin
            )
          )
        }
      },
      path("random") {
        get {
          complete(
            HttpEntity(
              ContentTypes.`text/html(UTF-8)`,
              numbers.map(n => ByteString(s"$n\n"))
            )
          )
        }
      },
    )

    val bindingFuture = Http().bindAndHandle(route, "localhost", PORT)

    println(s"Server online at http://localhost:$PORT\nPress RETURN to continue...")
    StdIn.readLine()
    bindingFuture
        .flatMap(_.unbind())
        .onComplete(_ => system.terminate())
    println("End stream sample")
    println()
  }
}
