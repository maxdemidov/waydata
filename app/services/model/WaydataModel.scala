package services.model

case class Distance(distance: Double)

case class Speed(speed: Double)

case class Location(lat: Double, lon: Double)

case class Point(timestamp: Long, speed: Speed, location: Location)

case class Report(averageSpeed: Speed, totalDistance: Distance, forPoints: List[Point])

case class Segment(fromPoint: Point, toPoint: Point)

case class Section(speed: Speed, distance: Distance)