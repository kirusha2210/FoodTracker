ThisBuild / organization := "com.foodtracker"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.7"

lazy val V = new {
  val catsEffect = "3.5.7"
  val http4s = "0.23.30"
  val doobie = "1.0.0-RC5"
  val sqlite = "3.46.1.3"
  val logback = "1.5.8"
}

lazy val root = (project in file("."))
  .settings(
    name := "FoodTracker",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % V.catsEffect,
      "org.http4s" %% "http4s-dsl" % V.http4s,
      "org.http4s" %% "http4s-ember-server" % V.http4s,
      "org.http4s" %% "http4s-ember-client" % V.http4s,
      "org.tpolecat" %% "doobie-core" % V.doobie,
      "org.xerial" % "sqlite-jdbc" % V.sqlite,
      "ch.qos.logback" % "logback-classic" % V.logback,
      "org.http4s" %% "http4s-circe" % V.http4s,
      "io.circe" %% "circe-generic" % "0.14.15",
      "org.scalatest" %% "scalatest" % "3.2.20" % "test",
    ),
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked"
    )
  )
