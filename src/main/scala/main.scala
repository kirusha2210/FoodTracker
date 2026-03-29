import cats.effect.{IO, IOApp}
import org.http4s.HttpRoutes
import org.http4s.MediaType
import org.http4s.dsl.io.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.headers.`Content-Type`
import org.http4s.server.Router

import scala.io.Source

object FoodTrackerApp extends IOApp.Simple:

  private val html =
    """<!DOCTYPE html>
      |<html lang="en">
      |  <head>
      |    <meta charset="UTF-8" />
      |    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      |    <title>Food Track</title>
      |    <link rel="preconnect" href="https://fonts.googleapis.com" />
      |    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
      |    <link
      |      href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@400;500;700&family=Instrument+Serif:ital@0;1&display=swap"
      |      rel="stylesheet"
      |    />
      |    <link rel="stylesheet" href="/styles.css" />
      |  </head>
      |  <body>
      |    <div class="page-shell">
      |      <header class="hero">
      |        <div class="hero-copy">
      |          <p class="eyebrow">Food Track</p>
      |          <h1>Track meals with a calm, fast daily ritual.</h1>
      |          <p class="hero-text">
      |            Log breakfast, lunch, dinner, and snacks in seconds while keeping your
      |            daily energy balance in view.
      |          </p>
      |          <div class="hero-actions">
      |            <button id="focus-form" class="primary-btn">Log today's meal</button>
      |            <span class="status-pill">Scala 3 + http4s starter</span>
      |          </div>
      |        </div>
      |        <aside class="hero-panel">
      |          <p class="panel-label">Today</p>
      |          <div class="macro-score">
      |            <span id="macro-percent">0%</span>
      |            <small>goal progress</small>
      |          </div>
      |          <div class="macro-bars">
      |            <div>
      |              <label>Calories</label>
      |              <progress id="calorie-progress" max="2200" value="0"></progress>
      |            </div>
      |            <div>
      |              <label>Water</label>
      |              <progress id="water-progress" max="3000" value="0"></progress>
      |            </div>
      |          </div>
      |        </aside>
      |      </header>
      |
      |      <main class="dashboard">
      |        <section class="summary-grid" id="summary-grid"></section>
      |
      |        <section class="content-grid">
      |          <section class="card form-card">
      |            <div class="section-heading">
      |              <p class="eyebrow">Quick Add</p>
      |              <h2>Capture a meal</h2>
      |            </div>
      |            <form id="meal-form" class="meal-form">
      |              <label>
      |                Meal name
      |                <input id="meal-name" name="mealName" type="text" placeholder="Salmon bowl" required />
      |              </label>
      |              <div class="form-row">
      |                <label>
      |                  Calories
      |                  <input id="meal-calories" name="calories" type="number" min="0" placeholder="520" required />
      |                </label>
      |                <label>
      |                  Time
      |                  <input id="meal-time" name="time" type="time" required />
      |                </label>
      |              </div>
      |              <label>
      |                Notes
      |                <textarea id="meal-notes" name="notes" rows="3" placeholder="Add protein, mood, or prep notes"></textarea>
      |              </label>
      |              <button class="primary-btn" type="submit">Add meal</button>
      |            </form>
      |          </section>
      |
      |          <section class="card meals-card">
      |            <div class="section-heading">
      |              <p class="eyebrow">Timeline</p>
      |              <h2>Today's meals</h2>
      |            </div>
      |            <ul id="meal-list" class="meal-list"></ul>
      |          </section>
      |        </section>
      |      </main>
      |    </div>
      |
      |    <script src="/app.js"></script>
      |  </body>
      |</html>
      |""".stripMargin

  private def loadResource(path: String): IO[String] =
    IO.blocking {
      val stream = Option(getClass.getResourceAsStream(path))
        .getOrElse(throw new IllegalStateException(s"Missing resource: $path"))

      try Source.fromInputStream(stream).mkString
      finally stream.close()
    }

  private val routes = HttpRoutes.of[IO] {
    case GET -> Root =>
      Ok(html, `Content-Type`(MediaType.text.html))

    case GET -> Root / "app.js" =>
      loadResource("/public/app.js").flatMap(js =>
        Ok(js, `Content-Type`(MediaType.application.javascript))
      )

    case GET -> Root / "styles.css" =>
      loadResource("/public/styles.css").flatMap(css =>
        Ok(css, `Content-Type`(MediaType.text.css))
      )
  }

  override def run: IO[Unit] =
    EmberServerBuilder
      .default[IO]
      .withHost(com.comcast.ip4s.Host.fromString("0.0.0.0").get)
      .withPort(com.comcast.ip4s.Port.fromInt(8080).get)
      .withHttpApp(Router("/" -> routes).orNotFound)
      .build
      .useForever
