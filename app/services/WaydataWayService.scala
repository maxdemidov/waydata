package services

import java.sql.Timestamp
import java.util.Date
import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.util.Timeout
import model.Way

import scala.concurrent.duration._
import repositories.postgres.WaydataWayRepository
import repositories.postgres.persistence.WaydataWay

import scala.concurrent.Future

@Singleton
class WaydataWayService @Inject()(actorSystem: ActorSystem,
                                  val waydataWayRepository: WaydataWayRepository) {

  implicit val timeout = Timeout(5 seconds)
  implicit val executionContext = actorSystem.dispatcher

  def addWay(way: Way): Future[Unit] =
    waydataWayRepository.addWay(toPersist(way))

  def getWayByUuid(wayUuid: java.util.UUID): Future[Option[Way]] =
    waydataWayRepository.getWayByUuid(wayUuid).map {
      case Seq() => None
      case Seq(waydataWay) => Some(toBusiness(waydataWay))
    }

  def getAllWaysByUserUuid(userUuid: java.util.UUID): Future[Seq[Way]] =
    waydataWayRepository.getAllWaysByUserUuid(userUuid).map(_.map(toBusiness))

  def getAllWays: Future[Seq[Way]] =
    waydataWayRepository.getAllWays.map(_.map(toBusiness))

  private def toPersist(way: Way): WaydataWay =
    WaydataWay(
      way.wayId,
      way.userId,
      way.name,
      way.created match {
        case null => null
        case created => new Timestamp(created.getTime)
      }
    )

  private def toBusiness(waydataWay: WaydataWay): Way =
    Way(
      waydataWay.wayUuid,
      waydataWay.userUuid,
      waydataWay.name,
      new Date(waydataWay.created.getTime)
    )
}
