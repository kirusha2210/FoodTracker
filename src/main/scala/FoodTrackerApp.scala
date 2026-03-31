import cats.effect.{IO, IOApp}
import repository.MealRepository

object FoodTrackerApp extends IOApp.Simple:
  
  def startServer(repository: MealRepository): IO[Unit] = IO.unit

  override def run: IO[Unit] =
    for {
      _ <- Database.init
      repo = new MealRepository(Database.xa)
      _ <- startServer(repo)
    } yield ()
