package controllers

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads, Writes}
import services.model._

trait WaydataSerializations {

  // writes

  implicit val speedWrites: Writes[Speed] =
    (JsPath \ "value").write[Double].contramap { (speed: Speed) => speed.value }

  implicit val distanceWrites: Writes[Distance] =
    (JsPath \ "value").write[Double].contramap { (distance: Distance) => distance.value }

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
      (JsPath \ "forPoints").write[Seq[Point]]
    )(unlift(Report.unapply))

  // reads

  implicit val speedReads: Reads[Speed] =
    (JsPath \ "value").read[Double].map { value => Speed(value) }

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
