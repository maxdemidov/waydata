package services

import java.util.{Date}

import repositories.WayPoint
import services.model.{Location, Point, Speed}

trait WaydataMapping {

  def mapWayPointToPoint(wayPoint: WayPoint): Point = {
    Point(
      wayPoint.createdOn.getTime,
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
      new Date(point.timestamp),
      point.speed.value,
      point.location.lat,
      point.location.lon
    )
  }
}
