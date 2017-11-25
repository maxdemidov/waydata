package repositories.postgres

import repositories.postgres.persistence.WaydataPoint
import slick.jdbc.GetResult

@deprecated
trait WayPointResult {

  implicit val getPointResult: AnyRef with GetResult[WaydataPoint] =
    GetResult(r => WaydataPoint(
      r.nextLong(),
      r.nextDouble(),
      r.nextFloat(),
      r.nextFloat()
    ))

  implicit val getRowResult: AnyRef with GetResult[(Long, Double, Float, Float)] =
    GetResult(r => (
      r.nextLong(),
      r.nextDouble(),
      r.nextFloat(),
      r.nextFloat()
    ))
}
