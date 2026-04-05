package service

trait ServingMeal {
  def weight(): Int
}

case class BigServing() extends ServingMeal {
  override def weight(): Int = 200
}

case class MiddleServing() extends ServingMeal {
  override def weight(): Int = 100
}

case class SmallServing() extends ServingMeal {
  override def weight(): Int = 50
}
