package service

import cats.effect.IO
import repository.{Food, MealRepository, MealSite, NewMeal}

class MealService(repository: MealRepository) {
  def create(food: Food, mealSite: MealSite): IO[Int] = {
    repository.create(combineMeal(food, mealSite))
  }

  def combineMeal(food: Food, mealSite: MealSite): NewMeal =
    NewMeal(
      name = mealSite.name,
      foodId = mealSite.foodId,
      portion = mealSite.portion,
      calories = getCalories(mealSite.portion, food),
      protein = getProtein(mealSite.portion, food),
      fat = getFat(mealSite.portion, food),
      carbs = getCarbs(mealSite.portion, food),
      eatenAt = mealSite.eatenAt,
      notes = mealSite.notes
    )

  private def getFat(portion: Int, food: Food): Int =
    food.fat * portion

  private def getProtein(portion: Int, food: Food): Int =
    food.protein * portion

  private def getCarbs(portion: Int, food: Food): Int =
    food.carbs * portion

  private def getCalories(portion: Int, food: Food): Int =
    food.calories * portion
}
