package repositories.postgres

import javax.inject.{Inject, Singleton}

import play.api.Logger
import repositories.postgres.persistence.{WaydataUserTable, WaydataWay, WaydataWayTable}
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}


@Singleton()
class WaydataWayRepository @Inject()(databaseProvider: DatabaseProvider,
                                     implicit val ec: ExecutionContext) {

  val users =
    TableQuery[WaydataUserTable]

  val ways =
    TableQuery[WaydataWayTable]

  def getWayByUuid(wayUuid: java.util.UUID): Future[Seq[WaydataWay]] = {
    databaseProvider.db.run(
      ways.filter(_.wayUuid === wayUuid).result
    )
  }

  def getAllWaysByUserUuid(userUuid: java.util.UUID): Future[Seq[WaydataWay]] = {
    databaseProvider.db.run(
      ways.filter(_.userUuid === userUuid).result
    )
  }

  def getAllWays: Future[Seq[WaydataWay]] = {
    databaseProvider.db.run(
      ways.result
    )
  }

  def addWay(way: WaydataWay): Future[Unit] = {
    databaseProvider.db.run(
      sqlu"""
          INSERT INTO public.waydata_way (
            way_uuid,
            user_uuid,
            way_name,
            way_created
          )
          VALUES (
            uuid_generate_v4(),
            ${way.userUuid.toString}::uuid,
            ${way.name},
            NOW()
          )
      """.transactionally
    ).map {
      response =>
        Logger.info(s"Added new way with name = ${way.name}")
    }.recover {
      case exception: java.sql.SQLException =>
        Logger.info("Caught exception when adding new way: " + exception.getMessage)
    }
  }
}
