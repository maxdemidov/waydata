package repositories

import java.util.Date

import slick.jdbc.GetResult

trait WayPointResult {

  implicit val getPointResult =
    GetResult(r => WayPoint(
      r.nextDate(),
      r.nextDouble(),
      r.nextDouble(),
      r.nextDouble()
    ))

  implicit val getPointsResult =
    GetResult(r => {

      // TODO - !! how to return list
      val rs = r.rs
      println("-> " + r.rs)

      val list = List(
        WayPoint(new Date(123), 1.1, 1.2, 1.3),
        WayPoint(new Date(456), 2.2, 2.1, 2.3),
        WayPoint(new Date(789), 3.3, 3.1, 3.2)
      )
      list
      //List[WayPoint]()
    })
}
