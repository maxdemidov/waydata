package repositories

import java.util.Date

import slick.jdbc.GetResult

trait WayPointResult {

  implicit val getPointResult =
    GetResult(r => WayPoint(
      r.nextDate(),
      r.nextDouble(),
      r.nextDouble(),
      r.nextDouble()
    ))

  implicit val getRowResult =
    GetResult(r => (
      r.nextDate(),
      r.nextDouble(),
      r.nextDouble(),
      r.nextDouble()
    ))
}
