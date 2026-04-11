package service

import cats.effect.IO
import clients.OpenFoodFactsClient
import repository.{Food, FoodRepository}

class FoodService(repository: FoodRepository, openFoodFactsClient: OpenFoodFactsClient) {
  def listAll(): IO[List[Food]] = repository.listAll
}