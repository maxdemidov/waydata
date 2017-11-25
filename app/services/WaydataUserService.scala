package services

import java.sql.Timestamp
import java.util.Date
import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Future
import repositories.postgres.WaydataUserRepository
import repositories.postgres.persistence.WaydataUser
import model.User

@Singleton
class WaydataUserService @Inject()(actorSystem: ActorSystem,
                                   val waydataUserRepository: WaydataUserRepository) {

  implicit val timeout = Timeout(5 seconds)
  implicit val executionContext = actorSystem.dispatcher

  def addUser(user: User): Future[Unit] =
    waydataUserRepository.addUser(toPersist(user))

  def getUserByUuid(userUuid: java.util.UUID): Future[Option[User]] =
    waydataUserRepository.getUserByUuid(userUuid).map {
      case Seq() => None
      case Seq(waydataUser) => Some(toBusiness(waydataUser))
    }

  def getAllUsers: Future[Seq[User]] =
    waydataUserRepository.getAllUsers.map(_.map(toBusiness))

  private def toPersist(user: User): WaydataUser =
    WaydataUser(
      user.userId,
      user.name,
      user.email,
      user.phone,
      user.created match {
        case null => null
        case created => new Timestamp(created.getTime)
      }
    )

  private def toBusiness(waydataUser: WaydataUser): User =
    User(
      waydataUser.userUuid,
      waydataUser.name,
      waydataUser.email,
      waydataUser.phone,
      new Date(waydataUser.created.getTime)
    )
}
