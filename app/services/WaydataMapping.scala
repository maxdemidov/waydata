package services

import repositories.WayPoint
import services.model.{Location, Point, Speed}

trait WaydataMapping {

  def mapSeqOfRowsToSeqOfPoint(seq: Seq[(Long ,Double, Double, Double)]): Seq[Point] = {
    seq.map(
      row =>
        Point(
          row._1,
          Speed(row._2),
          Location(row._3, row._4)
        )
    )
  }

  def mapWayPointToPoint(wayPoint: WayPoint): Point = {
    Point(
      wayPoint.createdOn,
      Speed(wayPoint.speed),
      Location(wayPoint.latitude, wayPoint.longitude)
    )
  }

  def mapWayPointToPointOption(optionWayPoint: Option[WayPoint]): Option[Point] = {
    optionWayPoint match {
      case None =>
        Option.empty[Point]
      case Some(wayPoint: WayPoint) =>
        Option.apply(
          mapWayPointToPoint(wayPoint)
        )
    }
  }

  def mapWayPointsToPointsOption(optionWayPoints: Option[List[WayPoint]]): Option[List[Point]] = {
    optionWayPoints match {
      case None =>
        Option.empty[List[Point]]
      case Some(wayPoints: List[WayPoint]) =>
        Option.apply(
          wayPoints.map(mapWayPointToPoint)
        )
    }
  }

  def mapPointToWayPoint(point: Point): WayPoint = {
    WayPoint(
      point.timestamp,
      point.speed.value,
      point.location.lat,
      point.location.lon
    )
  }
}
