package service

import cats.effect.IO
import repository.*

class MealService(repository: MealRepository, foodRepository: FoodRepository) {
  def create(mealInput: MealInput): IO[Int] = {
    combineMeal(mealInput).flatMap(repository.create)
  }

  def listAll(): IO[List[Meal]] = repository.listAll

  private def combineMeal(mealSite: MealInput): IO[NewMeal] =
    foodRepository.getById(mealSite.foodId).map { food =>
      NewMeal(
        name = mealSite.name,
        foodId = food.id,
        portion = mealSite.portion,
        calories = getCalories(mealSite.portion, food),
        protein = getProtein(mealSite.portion, food),
        fat = getFat(mealSite.portion, food),
        carbs = getCarbs(mealSite.portion, food),
        eatenAt = mealSite.eatenAt,
        notes = mealSite.notes
      )
    }
  
  private def getFat(portion: Int, food: Food): Int =
    food.fat * portion

  private def getProtein(portion: Int, food: Food): Int =
    food.protein * portion

  private def getCarbs(portion: Int, food: Food): Int =
    food.carbs * portion

  private def getCalories(portion: Int, food: Food): Int =
    food.calories * portion
}
