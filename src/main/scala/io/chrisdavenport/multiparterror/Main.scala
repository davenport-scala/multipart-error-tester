package io.chrisdavenport.multiparterror

// import cats._
// import cats.implicits._
import cats.effect._
import fs2._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.multipart._
import org.http4s.server.blaze.BlazeBuilder
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends StreamApp[IO] {
  val service = {
    val dsl = new Http4sDsl[IO]{}
    import dsl._

    HttpService[IO]{
      case req => 
        val multipart : IO[Multipart[IO]] = req.as[Multipart[IO]]
        for {
          m <- multipart
          _ <- IO(println(m))
          resp <- Ok(m)
        } yield resp
    }
  }

  def stream(args: List[String], requestShutdown: IO[Unit]) = for {
    exitCode <- BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(service, "/")
      .serve 
  } yield exitCode

}