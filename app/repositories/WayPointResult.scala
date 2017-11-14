package repositories

import slick.jdbc.GetResult

trait WayPointResult {

  implicit val getPointResult =
    GetResult(r => WayPoint(
      r.nextLong(),
      r.nextDouble(),
      r.nextDouble(),
      r.nextDouble()
    ))

  implicit val getRowResult =
    GetResult(r => (
      r.nextLong(),
      r.nextDouble(),
      r.nextDouble(),
      r.nextDouble()
    ))
}
