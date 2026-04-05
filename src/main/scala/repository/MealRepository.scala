package repository

import cats.effect.IO
import doobie.implicits.*
import doobie.util.meta.Meta
import doobie.util.transactor.Transactor

case class Meal(
                       id: Long,
                       name: String,
                       foodId: Long,
                       portion: Int,
                       calories: Int,
                       protein: Int,
                       fat: Int,
                       carbs: Int,
                       eatenAt: String,
                       notes: Option[String],
                       createdAt: String
                     )

case class MealSite(
                       name: String,
                       foodId: Long,
                       portion: Int,
                       calories: Int,
                       eatenAt: String,
                       notes: Option[String],
                       createdAt: String
                     )

final case class NewMeal(
                    name: String,
                    foodId: Long,
                    portion: Int,
                    calories: Int,
                    protein: Int,
                    fat: Int,
                    carbs: Int,
                    eatenAt: String,
                    notes: Option[String]
                  )

class MealRepository(xa: Transactor[IO]) {
  def create(meal: NewMeal): IO[Int] = {
    sql"""
         |INSERT INTO meals (name, food_id, calories, protein, fat, carbs, eaten_at, notes, created_at)
         |values (
         |  ${meal.name},
         |  ${meal.foodId},
         |  ${meal.calories},
         |  ${meal.protein},
         |  ${meal.fat},
         |  ${meal.carbs},
         |  ${meal.eatenAt},
         |  ${meal.notes},
         |  datetime('now')
         |)
         |""".stripMargin.update.run.transact(xa)
  }

  def listAll(): IO[List[Meal]] = {
    sql"""
         select id, name, food_id, calories, eaten_at, notes, created_at
         from meals
         order by eaten_at desc
       """
      .query[Meal].to[List]
      .transact(xa)
  }
}
