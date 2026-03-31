package repository

import cats.effect.IO
import doobie.implicits.*
import doobie.util.transactor.Transactor

final case class Meal(
                       id: Long,
                       name: String,
                       calories: Int,
                       eatenAt: String,
                       notes: Option[String],
                       createdAt: String
                     )

final case class NewMeal(
                    name: String,
                    calories: Int,
                    eatenAt: String,
                    notes: Option[String]
                  )

final class MealRepository(xa: Transactor[IO]) {
  def create(meal: NewMeal): IO[Int] = {
    sql"""
         |INSERT INTO meals (name, calories, eaten_at, notes, created_at)
         |values (
         |  ${meal.name},
         |  ${meal.calories},
         |  ${meal.eatenAt}
         |  ${meal.notes},
         |)
         |""".stripMargin.update.run.transact(xa)
  }

  def listAll(): IO[List[Meal]] = {
    sql"""
         select id, name, calories, eaten_at, notes, created_at
         from meals
         order by eaten_at desc
       """.query[Meal].to[List].transact(xa)
  }
}