package routes

import cats.effect.IO
import org.http4s.{HttpRoutes, MediaType}
import org.http4s.dsl.io.*
import org.http4s.headers.`Content-Type`

import scala.io.Source

class MealRoutes {
  private def loadResource(path: String): IO[String] =
    IO.blocking {
      val stream = Option(getClass.getResourceAsStream(path))
        .getOrElse(throw new IllegalStateException(s"Missing resource: $path"))

      try Source.fromInputStream(stream).mkString
      finally stream.close()
    }

  private val routes = HttpRoutes.of[IO] {
    case GET -> Root =>
      loadResource("/public/index.html").flatMap(html =>
        Ok(html, `Content-Type`(MediaType.text.html))
      )

    case GET -> Root / "app.js" =>
      loadResource("/public/app.js").flatMap(js =>
        Ok(js, `Content-Type`(MediaType.application.javascript))
      )

    case GET -> Root / "styles.css" =>
      loadResource("/public/styles.css").flatMap(css =>
        Ok(css, `Content-Type`(MediaType.text.css))
      )
  }
}
