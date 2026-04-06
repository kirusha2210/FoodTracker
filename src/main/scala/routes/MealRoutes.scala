package routes

import cats.effect.IO
import io.circe.generic.auto.*
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.dsl.io.*
import org.http4s.headers.`Content-Type`
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes, MediaType}
import repository.{Meal, MealInput, NewMeal}
import service.MealService

import scala.io.Source

class MealRoutes(service: MealService) {
  given EntityDecoder[IO, NewMeal] = jsonOf[IO, NewMeal]
  given EntityDecoder[IO, MealInput] = jsonOf[IO, MealInput]
  given EntityEncoder[IO, List[Meal]] = jsonEncoderOf[IO, List[Meal]]
  
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
      req.as[MealInput].flatMap { mealInput =>
        service.create(mealInput).flatMap(_ => Created())
      }

    case GET -> Root / "meals" =>
      service.listAll().flatMap(meals => Ok(meals))
  }
}
