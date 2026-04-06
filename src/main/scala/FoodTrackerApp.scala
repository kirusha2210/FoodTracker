import cats.data.Kleisli
import cats.effect.{IO, IOApp}
import com.comcast.ip4s.{host, port}
import db.Database
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.http4s.{Request, Response}
import repository.{FoodRepository, MealRepository}
import routes.{FoodRoutes, MealRoutes}
import service.{FoodService, MealService}

object FoodTrackerApp extends IOApp.Simple:
  
  def startServer(mealService: MealService, foodService: FoodService): IO[Unit] = {
    EmberServerBuilder
      .default[IO]
      .withHost(host"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(httpRoutes(mealService, foodService))
      .build
      .useForever
  }

  def httpRoutes(mealService: MealService, foodService: FoodService): Kleisli[IO, Request[IO], Response[IO]] =
    Router(
      "/" -> new MealRoutes(mealService).routes,
      "/foods" -> new FoodRoutes(foodService).routes,
    ).orNotFound

  override def run: IO[Unit] =
    for {
      _ <- Database.init

      mealRepository = new MealRepository(Database.xa)
      foodRepository = new FoodRepository(Database.xa)

      mealService = new MealService(mealRepository, foodRepository)
      foodService = new FoodService(foodRepository)

      _ <- startServer(mealService, foodService)
    } yield ()
