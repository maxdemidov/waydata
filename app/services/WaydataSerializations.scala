package services

import play.api.libs.json.{Reads, JsPath, Writes}
import play.api.libs.functional.syntax._
import services.model._

trait WaydataSerializations {

  // writes

  implicit val speedWrites: Writes[Speed] =
    (JsPath \ "speed").write[Double].contramap { (speed: Speed) => speed.speed }

  implicit val distanceWrites: Writes[Distance] =
    (JsPath \ "distance").write[Double].contramap { (distance: Distance) => distance.distance }

  implicit val locationWrites: Writes[Location] = (
    (JsPath \ "lat").write[Double] and
      (JsPath \ "lon").write[Double]
    )(unlift(Location.unapply))

  implicit val pointWrites: Writes[Point] = (
    (JsPath \ "timestamp").write[Long] and
      (JsPath \ "speed").write[Speed] and
      (JsPath \ "location").write[Location]
    )(unlift(Point.unapply))

  implicit val reportWrites: Writes[Report] = (
    (JsPath \ "averageSpeed").write[Speed] and
      (JsPath \ "totalDistance").write[Distance] and
      (JsPath \ "forPoints").write[List[Point]]
    )(unlift(Report.unapply))

  // reads

  implicit val speedReads: Reads[Speed] =
    (JsPath \ "speed").read[Double].map { speed => Speed(speed) }

  implicit val locationReads: Reads[Location] = (
    (JsPath \ "lat").read[Double] and
      (JsPath \ "lon").read[Double]
    )(Location.apply _)

  implicit val pointReads: Reads[Point] = (
    (JsPath \ "timestamp").read[Long] and
      (JsPath \ "speed").read[Speed] and
      (JsPath \ "location").read[Location]
    )(Point.apply _)
}
