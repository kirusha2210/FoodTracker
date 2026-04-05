import org.scalatest.funsuite.AnyFunSuite

class Test extends AnyFunSuite {
  test("test1") {
    def sum(cost: Int, limit: Int, costOverLimit: Int, use: Int): Unit = {
      val overLimit = use - limit
      println(
        if (overLimit <= 0) cost
        else cost + overLimit * costOverLimit
      )
    }

    sum(100, 10, 12, 1)
  }
}
