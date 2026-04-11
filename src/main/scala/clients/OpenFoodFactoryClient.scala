package clients

import cats.effect.IO
import org.http4s.{EntityDecoder, Header, Method, Request}
import org.http4s.circe.jsonOf
import org.http4s.client.Client
import org.http4s.implicits.uri
import repository.FoodRepository
import io.circe.generic.auto.*
import org.typelevel.ci.CIStringSyntax

class OpenFoodFactsClient(client: Client[IO]):
  given EntityDecoder[IO, OpenFoodFactsResponse] = jsonOf[IO, OpenFoodFactsResponse]

  private val userAgent = "FoodTracker/0.1 (k.lebedev@gmail.com)"

  def searchProducts(query: String): IO[List[FoodSearchResult]] = {
    val uri =
      uri"https://world.openfoodfacts.org/cgi/search.pl"
        .withQueryParam("search_terms", query)
        .withQueryParam("search_simple", 1)
        .withQueryParam("json", 1)
        .withQueryParam("page_size", 20)
        .withQueryParam("fields", "code,product_name,brands,image_url,countries_tags,nutriments")

    val request =
      Request[IO](Method.GET, uri)
        .putHeaders(Header.Raw(ci"User-Agent", userAgent))

    client.expect[OpenFoodFactsResponse](request).map { response =>
      response.products
        .filter(product => hasRuCountry(product))
        .map(toFoodSearchResult)
    }
  }

  private def toFoodSearchResult(product: OpenFoodFactsProduct): FoodSearchResult = {
    FoodSearchResult(
      externalId = product.code.getOrElse(""),
      name = product.product_name.getOrElse(""),
      brand = product.brands,
      calories = product.nutriments.flatMap(_.`energy-kcal_100g`),
      protein = product.nutriments.flatMap(_.proteins_100g),
      fat = product.nutriments.flatMap(_.fat_100g),
      carbs = product.nutriments.flatMap(_.carbohydrates_100g),
      imageUrl = product.image_url
    )
  }

  private def hasRuCountry(product: OpenFoodFactsProduct): Boolean =
    product.countriesTags.exists(tags =>
      tags.exists(tag =>
        tag == "en:russia" || tag == "ru:россия"
      )
    )

  final case class OpenFoodFactsResponse(
                                          products: List[OpenFoodFactsProduct]
                                        )

  final case class OpenFoodFactsProduct(
                                         code: Option[String],
                                         product_name: Option[String],
                                         brands: Option[String],
                                         image_url: Option[String],
                                         countriesTags: Option[List[String]],
                                         nutriments: Option[Nutriments]
                                       )

  final case class Nutriments(
                               `energy-kcal_100g`: Option[Double],
                               proteins_100g: Option[Double],
                               fat_100g: Option[Double],
                               carbohydrates_100g: Option[Double]
                             )

final case class FoodSearchResult(
                                   externalId: String,
                                   name: String,
                                   brand: Option[String],
                                   calories: Option[Double],
                                   protein: Option[Double],
                                   fat: Option[Double],
                                   carbs: Option[Double],
                                   imageUrl: Option[String]
                                 )