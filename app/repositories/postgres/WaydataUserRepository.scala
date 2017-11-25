package repositories.postgres

import javax.inject.{Inject, Singleton}

import play.api.Logger
import repositories.postgres.persistence.{WaydataUser, WaydataUserTable}
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class WaydataUserRepository @Inject()(databaseProvider: DatabaseProvider,
                                      implicit val ec: ExecutionContext) {

  val users =
    TableQuery[WaydataUserTable]

  def getUserByUuid(userUuid: java.util.UUID): Future[Seq[WaydataUser]] = {
    databaseProvider.db.run(
      users.filter(_.userUuid === userUuid).result
    )
  }

  def getAllUsers: Future[Seq[WaydataUser]] = {
    databaseProvider.db.run(
      users.result
    )
  }

  // TODO
  def addUser(user: WaydataUser): Future[Unit] = {
    databaseProvider.db.run(
      sqlu"""
          INSERT INTO public.waydata_user (
            user_uuid,
            user_email,
            user_phone,
            user_name,
            user_created
          )
          VALUES (
            uuid_generate_v4(),
            ${user.email},
            ${user.phone},
            ${user.name},
            NOW()
          )
      """.transactionally
    ).map {
      response =>
        Logger.info(s"Added new user with email = ${user.email} and phone = ${user.phone}")
    }.recover {
      case exception: java.sql.SQLException =>
        Logger.info("Caught exception when adding new user: " + exception.getMessage)
    }
  }
}
