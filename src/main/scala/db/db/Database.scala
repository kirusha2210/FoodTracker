package db

import cats.effect.{IO, Resource}
import doobie.*
import doobie.implicits.*
import doobie.util.transactor.Transactor

import java.nio.file.{Files, Paths}

object Database {
  private val dbPath = "src/main/resources/data/foodtracker.db"
  private val jdbcUrl = s"jdbc:sqlite:$dbPath"
  private val user = "user"
  private val password = "user"

  val xa: Transactor[IO] = {
    Transactor.fromDriverManager[IO](
      driver = "org.sqlite.JDBC",
      url = jdbcUrl,
      user = "",
      password = "",
      logHandler = None
    )
  }
  
  def init: IO[Unit] =
    sql"""
        create table if not exists meals (
          id integer primary key autoincrement,
          name text not null,
          calories integer not null,
          eaten_at text not null,
          notes text,
          created_at text not null
        )
      """.update.run.transact(xa).void
}
