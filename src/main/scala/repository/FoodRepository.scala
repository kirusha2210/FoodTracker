package repository

import cats.effect.IO
import doobie.implicits.*
import doobie.util.transactor.Transactor
import doobie.util.update.Update

case class Food(
                 id: Long,
                 name: String,
                 description: String,
                 calories: Int,
                 protein: Int,
                 fat: Int,
                 carbs: Int,
               )

case class NewFood(
                    name: String,
                    description: String,
                    calories: Int,
                    protein: Int,
                    fat: Int,
                    carbs: Int,
                  )

class FoodRepository(xa: Transactor[IO]) {
  def create(newFood: NewFood): IO[Int] = {
    sql"""
          insert into foods(name, description, carbs, calories, protein, fat)
          values (?, ?, ?, ?, ?, ?)
       """
      .update.run.transact(xa)
  }
  
  def createFoods(newFoods: List[NewFood]): IO[Int] = {
    val sql = """
          insert into foods(name, description, carbs, calories, protein, fat)
          values (?, ?, ?, ?, ?, ?)
       """

    Update[NewFood](sql).updateMany(newFoods).transact(xa)
  }

  def listAll: IO[List[Food]] = {
    sql"""
         select id, name, description, calories, protein, fat, carbs
         from foods
       """
      .query[Food].to[List]
      .transact(xa)
  }
  
  def getById(id: Long): IO[Food] = {
    sql"""select id, name, description, calories, protein, fat, carbs 
          from foods
          where id = $id
          """.stripMargin.query[Food].unique.transact(xa)
  }
}
