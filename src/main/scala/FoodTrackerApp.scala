import cats.effect.{IO, IOApp}
import com.comcast.ip4s.{host, port}
import db.Database
import org.http4s.ember.server.EmberServerBuilder
import repository.MealRepository
import routes.MealRoutes

object FoodTrackerApp extends IOApp.Simple:
  
  def startServer(repository: MealRepository): IO[Unit] = {
    EmberServerBuilder
      .default[IO]
      .withHost(host"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(new MealRoutes(repository).routes.orNotFound)
      .build
      .useForever
  }

  override def run: IO[Unit] =
    for {
      _ <- Database.init
      repo = new MealRepository(Database.xa)
      _ <- startServer(repo)
    } yield ()
