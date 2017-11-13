package repositories

import java.util.Date

// TODO - use TableQuery instead SQL
//trait WayPointsTable {
//  import slick.driver.PostgresDriver.api._
//
//  class WayPoints(tag: Tag) extends Table[(Date, Double, Double, Double)](tag, "way_point") {
//    def createdOn = column[Date]("created_on", O.PrimaryKey)
//    def speed = column[Double]("speed")
//    def latitude = column[Double]("latitude")
//    def longitude = column[Double]("longitude")
//    def * = (createdOn, speed, latitude, longitude)
//  }
//
//  val points = TableQuery[WayPoints]
//}

case class WayPoint(createdOn: Date, speed: Double, latitude: Double, longitude: Double)


