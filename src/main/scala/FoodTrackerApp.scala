import cats.data.Kleisli
import cats.effect.{IO, IOApp}
import clients.OpenFoodFactsClient
import com.comcast.ip4s.{host, port}
import db.Database
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.http4s.{Request, Response}
import repository.{FoodRepository, MealRepository}
import routes.{FoodRoutes, MealRoutes}
import service.{FoodService, MealService}

object FoodTrackerApp extends IOApp.Simple:
  
  private def startServer(mealService: MealService, foodService: FoodService): IO[Unit] = {
    EmberServerBuilder
      .default[IO]
      .withHost(host"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(
        Router(
          "/" -> new MealRoutes(mealService).routes,
          "/foods" -> new FoodRoutes(foodService).routes,
        ).orNotFound
      )
      .build
      .useForever
  }
  
  private def startClient(init: Client[IO] => IO[Unit]): IO[Unit] = {
    EmberClientBuilder.default[IO]
      .build
      .use(init)
  }

  private def init(httpClient: Client[IO]): IO[Unit] = {
    for {
      _ <- Database.init

      openFoodFactsClient = OpenFoodFactsClient(httpClient)
      mealRepository = new MealRepository(Database.xa)
      foodRepository = new FoodRepository(Database.xa)

      mealService = new MealService(mealRepository, foodRepository)
      foodService = new FoodService(foodRepository, openFoodFactsClient)

      _ <- startServer(mealService, foodService)
    } yield ()
  }

  override def run: IO[Unit] = startClient(init)
