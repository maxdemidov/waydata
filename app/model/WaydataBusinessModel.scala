package model


case class Distance(value: Double)

case class Speed(value: Double)



case class Location(lat: Float,
                    lon: Float)

case class Point(timestamp: Long,
                 speed: Speed,
                 location: Location)



case class User(userId: java.util.UUID,
                name: String,
                email: Option[String],
                phone: Option[String],
                created: java.util.Date)

case class Way(wayId: java.util.UUID,
               userId: java.util.UUID,
               name: String,
               created: java.util.Date)

case class UserWayPoint(userId: java.util.UUID,
                        wayId: java.util.UUID,
                        point: Point)



case class Report(averageSpeed: Speed,
                  totalDistance: Distance,
                  forPoints: Seq[Point])



case class Segment(fromPoint: Point,
                   toPoint: Point)

case class Section(millis: Long,
                   speed: Speed,
                   distance: Distance)