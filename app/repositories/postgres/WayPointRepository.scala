package repositories.postgres

import javax.inject.{Inject, Singleton}

import play.api.Logger
import repositories.postgres.persistence.WaydataPoint
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class WayPointRepository @Inject()(databaseProvider: DatabaseProvider,
                                   implicit val ec: ExecutionContext)
    extends WayPointResult
//    with WayPointsTable
  {

// TODO - use TableQuery instead SQL
//  def findByDate(date: Long) = {
//    val query = points.filter(point => point.createdOn)
//      .map(p => (p.createdOn, p.speed, p.latitude, p.longitude))
//    databaseProvider.db.run(query.result.headOption)
//  }

  def save(wayPoint: WaydataPoint): Future[Unit] = {
    databaseProvider.db.run(
      sqlu"""
          INSERT INTO public.way_point (created_on, speed, latitude, longitude)
          VALUES (to_timestamp(${wayPoint.createdOn}::double precision / 1000),
                  ${wayPoint.speed},
                  ${wayPoint.latitude},
                  ${wayPoint.longitude})
      """.transactionally
    ).map {
      response =>
        Logger.info(s"Added new point with timestamp ${wayPoint.createdOn}")
    }.recover {
      case exception: java.sql.SQLException =>
        Logger.info("Caught exception when adding new point: " + exception.getMessage)
    }
  }

  def findByInterval(fromDate: Long, toDate: Long): Future[Seq[(Long, Double, Float, Float)]] = {
    val selectQuery =
      sql"""
          SELECT
            EXTRACT(EPOCH FROM created_on AT TIME ZONE 'UTC') * 1000,
            speed,
            latitude,
            longitude
          FROM public.way_point
          WHERE created_on
            BETWEEN to_timestamp($fromDate::double precision / 1000)
                AND to_timestamp($toDate::double precision / 1000)
        """.as[(Long, Double, Float, Float)]
    databaseProvider.db.run(selectQuery)
  }

  def findAll(): Future[Seq[(Long, Double, Float, Float)]] = {
    val selectQuery =
      sql"""
          SELECT
            EXTRACT(EPOCH FROM created_on AT TIME ZONE 'UTC') * 1000,
            speed,
            latitude,
            longitude
          FROM public.way_point
        """.as[(Long, Double, Float, Float)]
    databaseProvider.db.run(selectQuery)
  }

  def findByTimestamp(timestamp: Long): Future[Option[WaydataPoint]] = {
    val selectQuery =
      sql"""
          SELECT
            EXTRACT(EPOCH FROM created_on AT TIME ZONE 'UTC') * 1000,
            speed,
            latitude,
            longitude
          FROM public.way_point
          WHERE created_on = to_timestamp($timestamp::double precision / 1000)
      """.as[WaydataPoint]
    for {
      item <- databaseProvider.db.run(selectQuery.headOption)
    } yield item
  }
}
