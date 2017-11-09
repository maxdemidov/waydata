package services

import services.model.{Point, Speed, Location}

// TODO - use db and slick
object WaydataPointRepository {

  val example =
    Point(
      1510158807,
      Speed(52.3),
      Location(51.30, 31.18)
    )

  var list: List[Point] = List(
    Point(
      1510158807,
      Speed(40.8),
      Location(51.30, 31.18)
    ),
    Point(
      1510158887,
      Speed(51.3),
      Location(51.40, 31.28)
    ),
    Point(
      1510158889,
      Speed(0.3),
      Location(51.40, 31.28)
    ),
    Point(
      1510158908,
      Speed(52.3),
      Location(51.20, 31.28)
    ),
    Point(
      1510159909,
      Speed(11.2),
      Location(51.40, 31.08)
    )
  )

  def save(place: Point) = {
    list = list ::: List(place)
  }
}
