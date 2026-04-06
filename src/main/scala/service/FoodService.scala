package service

import cats.effect.IO
import repository.{Food, FoodRepository}

class FoodService(repository: FoodRepository) {
  def listAll(): IO[List[Food]] = repository.listAll
}