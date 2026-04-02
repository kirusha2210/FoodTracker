package routes

import cats.effect.IO
import org.http4s.circe.jsonOf
import org.http4s.{ContextRequest, EntityDecoder, HttpRoutes, MediaType}
import org.http4s.dsl.io.*
import org.http4s.headers.`Content-Type`
import repository.{MealRepository, NewMeal}

import scala.io.Source

class MealRoutes(repository: MealRepository) {
  given EntityDecoder[IO, NewMeal] = jsonOf[IO, NewMeal]
  private def loadResource(path: String): IO[String] =
    IO.blocking {
      val stream = Option(getClass.getResourceAsStream(path))
        .getOrElse(throw new IllegalStateException(s"Missing resource: $path"))

      try Source.fromInputStream(stream).mkString
      finally stream.close()
    }

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
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

    case req @ POST -> Root / "meals" =>
      req.as[NewMeal].flatMap { meal =>
        repository.create(meal).flatMap(_ => Created())
      }
  }
}
