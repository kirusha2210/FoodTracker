ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.7"

lazy val root = (project in file("."))
  .settings(
    name := "FoodTracker"
  )

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-ember-server" % "0.23.33",
  "org.http4s" %% "http4s-dsl" % "0.23.33",
  "org.typelevel" %% "cats-effect" %        "3.7.0",

  "org.xerial" % "sqlite-jdbc" % "3.51.3.0",
  "org.tpolecat" %% "doobie-core" % "1.0.0-RC12",
  "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC12",

  "org.scalatest" %% "scalatest" % "3.2.20" % "test"
)