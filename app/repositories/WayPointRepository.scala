package repositories

import javax.inject.{Inject, Singleton}

import slick.driver.PostgresDriver.api._
import scala.concurrent.{ExecutionContext, Future}
import play.api.Logger

@Singleton()
class WayPointRepository @Inject()(databaseProvider: DatabaseProvider,
                                   implicit val ec : ExecutionContext)
  extends WayPointResult
//    with WayPointsTable
  {

// TODO - use TableQuery instead SQL
//  def findByDate(date: Long) = {
//    val query = points.filter(point => point.createdOn)
//      .map(p => (p.createdOn, p.speed, p.latitude, p.longitude))
//    databaseProvider.db.run(query.result.headOption)
//  }

  def save(wayPoint: WayPoint): Future[Unit] = {
    databaseProvider.db.run(
      sqlu"""
          INSERT INTO public.way_point (created_on, speed, latitude, longitude)
          VALUES (to_timestamp(${wayPoint.createdOn}),
                  ${wayPoint.speed},
                  ${wayPoint.latitude},
                  ${wayPoint.longitude})
      """.transactionally
    ).map {
      res =>
        Logger.info(s"Added new point with timestamp ${wayPoint.createdOn}")
    }.recover {
      case e: java.sql.SQLException =>
        Logger.info("Caught exception when adding new point: " + e.getMessage)
    }
  }

  def findByInterval(fromDate: Long, toDate: Long): Future[Seq[(Long ,Double, Double, Double)]] = {
    val selectQuery =
      sql"""
          SELECT
            EXTRACT(EPOCH FROM created_on AT TIME ZONE 'UTC'),
            speed,
            latitude,
            longitude
          FROM public.way_point
          WHERE created_on BETWEEN to_timestamp($fromDate) AND to_timestamp($toDate)
        """.as[(Long, Double, Double, Double)]
    databaseProvider.db.run(selectQuery)
  }

  def findAll(): Future[Seq[(Long ,Double, Double, Double)]] = {
    val selectQuery =
      sql"""
          SELECT
            EXTRACT(EPOCH FROM created_on AT TIME ZONE 'UTC'),
            speed,
            latitude,
            longitude
          FROM public.way_point
        """.as[(Long, Double, Double, Double)]
    databaseProvider.db.run(selectQuery)
  }

  def findByTimestamp(date: Long): Future[Option[WayPoint]] = {
    val selectQuery =
      sql"""
          SELECT
            EXTRACT(EPOCH FROM created_on AT TIME ZONE 'UTC'),
            speed,
            latitude,
            longitude
          FROM public.way_point
          WHERE created_on = to_timestamp($date)
      """.as[WayPoint]
    for {
      item <- databaseProvider.db.run(selectQuery.headOption)
    } yield item
  }
}
