package routes

import cats.effect.IO
import io.circe.generic.auto.*
import org.http4s.circe.jsonEncoderOf
import org.http4s.dsl.io.*
import org.http4s.{EntityEncoder, HttpRoutes}
import repository.Food
import service.FoodService

class FoodRoutes(service: FoodService) {
  given EntityEncoder[IO, List[Food]] = jsonEncoderOf[IO, List[Food]]

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "foods" =>
      service.listAll().flatMap(foods => Ok(foods))
  }
}
