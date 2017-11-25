package model

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads, Writes}

trait WaydataSerializations {

  // writes

  implicit val speedWrites: Writes[Speed] =
    (JsPath \ "value").write[Double].contramap { (speed: Speed) => speed.value }

  implicit val distanceWrites: Writes[Distance] =
    (JsPath \ "value").write[Double].contramap { (distance: Distance) => distance.value }

  implicit val locationWrites: Writes[Location] = (
      (JsPath \ "lat").write[Float] and
      (JsPath \ "lon").write[Float]
    )(unlift(Location.unapply))

  implicit val pointWrites: Writes[Point] = (
      (JsPath \ "timestamp").write[Long] and
      (JsPath \ "speed").write[Speed] and
      (JsPath \ "location").write[Location]
    )(unlift(Point.unapply))

  implicit val userWrites: Writes[User] = (
      (JsPath \ "userId").write[java.util.UUID] and
      (JsPath \ "name").write[String] and
      (JsPath \ "email").write[Option[String]] and
      (JsPath \ "phone").write[Option[String]] and
      (JsPath \ "created").write[java.util.Date]
    )(unlift(User.unapply))

  implicit val wayWrites: Writes[Way] = (
      (JsPath \ "wayId").write[java.util.UUID] and
      (JsPath \ "userId").write[java.util.UUID] and
      (JsPath \ "name").write[String] and
      (JsPath \ "created").write[java.util.Date]
    )(unlift(Way.unapply))

  implicit val userWayPointWrites: Writes[UserWayPoint] = (
      (JsPath \ "userId").write[java.util.UUID] and
      (JsPath \ "wayId").write[java.util.UUID] and
      (JsPath \ "point").write[Point]
    )(unlift(UserWayPoint.unapply))

  implicit val reportWrites: Writes[Report] = (
      (JsPath \ "averageSpeed").write[Speed] and
      (JsPath \ "totalDistance").write[Distance] and
      (JsPath \ "forPoints").write[Seq[Point]]
    )(unlift(Report.unapply))

  // reads

  implicit val speedReads: Reads[Speed] =
    (JsPath \ "value").read[Double].map { value => Speed(value) }

  implicit val locationReads: Reads[Location] = (
      (JsPath \ "lat").read[Float] and
      (JsPath \ "lon").read[Float]
    )(Location.apply _)

  implicit val pointReads: Reads[Point] = (
      (JsPath \ "timestamp").read[Long] and
      (JsPath \ "speed").read[Speed] and
      (JsPath \ "location").read[Location]
    )(Point.apply _)

  implicit val userReads: Reads[User] = (
      Reads.pure(null:java.util.UUID) and
      (JsPath \ "name").read[String] and
      (JsPath \ "email").readNullable[String] and
      (JsPath \ "phone").readNullable[String] and
      Reads.pure(null:java.util.Date)
    )(User.apply _)

  implicit val wayReads: Reads[Way] = (
      Reads.pure(null:java.util.UUID) and
      (JsPath \ "userId").read[java.util.UUID] and
      (JsPath \ "name").read[String] and
      Reads.pure(null:java.util.Date)
    )(Way.apply _)

  implicit val userWayPointReads: Reads[UserWayPoint] = (
      (JsPath \ "userId").read[java.util.UUID] and
      (JsPath \ "wayId").read[java.util.UUID] and
      (JsPath \ "point").read[Point]
    )(UserWayPoint.apply _)
}
