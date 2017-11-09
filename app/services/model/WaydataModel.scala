package services.model

case class Distance(value: Double)

case class Speed(value: Double)

case class Location(lat: Double, lon: Double)

case class Point(timestamp: Long, speed: Speed, location: Location)

case class Report(averageSpeed: Speed, totalDistance: Distance, forPoints: List[Point])

case class Segment(fromPoint: Point, toPoint: Point)

case class Section(millis: Long, speed: Speed, distance: Distance)