import cats.effect.{IO, IOApp}
import com.comcast.ip4s.{host, port}
import db.Database
import org.http4s.ember.server.EmberServerBuilder
import repository.{FoodRepository, MealRepository}
import routes.MealRoutes
import service.MealService

object FoodTrackerApp extends IOApp.Simple:
  
  def startServer(service: MealService): IO[Unit] = {
    EmberServerBuilder
      .default[IO]
      .withHost(host"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(new MealRoutes(service).routes.orNotFound)
      .build
      .useForever
  }

  override def run: IO[Unit] =
    for {
      _ <- Database.init
      mealRepository = new MealRepository(Database.xa)
      foodRepository = new FoodRepository(Database.xa)
      mealService = new MealService(mealRepository)
      _ <- startServer(mealService)
    } yield ()
