package services

import repositories.postgres.persistence.WaydataPoint
import model.{Location, Point, Speed}

trait WaydataMapping {

  def mapSeqOfRowsToSeqOfPoint(seq: Seq[(Long, Double, Float, Float)]): Seq[Point] = {
    seq.map(
      row =>
        Point(
          row._1,
          Speed(row._2),
          Location(row._3, row._4)
        )
    )
  }

  def mapWayPointToPoint(wayPoint: WaydataPoint): Point = {
    Point(
      wayPoint.createdOn,
      Speed(wayPoint.speed),
      Location(wayPoint.latitude, wayPoint.longitude)
    )
  }

  def mapWayPointToPointOption(optionWayPoint: Option[WaydataPoint]): Option[Point] = {
    optionWayPoint match {
      case None =>
        Option.empty[Point]
      case Some(wayPoint: WaydataPoint) =>
        Option.apply(
          mapWayPointToPoint(wayPoint)
        )
    }
  }

  def mapWayPointsToPointsOption(optionWayPoints: Option[List[WaydataPoint]]): Option[List[Point]] = {
    optionWayPoints match {
      case None =>
        Option.empty[List[Point]]
      case Some(wayPoints: List[WaydataPoint]) =>
        Option.apply(
          wayPoints.map(mapWayPointToPoint)
        )
    }
  }

  def mapPointToWayPoint(point: Point): WaydataPoint = {
    WaydataPoint(
      point.timestamp,
      point.speed.value,
      point.location.lat,
      point.location.lon
    )
  }
}
