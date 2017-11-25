package services

import javax.inject.{Inject, Singleton}

import akka.pattern.ask
import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import play.api.Logger

import scala.concurrent.Future
import scala.concurrent.duration._
import services.actors.CalculationActor
import services.actors.CalculationActor.{CalculationResponse, CalculationResults, CalculationUp}
import repositories.cassandra.UserWayPointsRepository
import repositories.postgres.WayPointRepository
import services.actors.CalculationActor.CalculationError
import model.{Distance, Point, Report, Speed}

@Singleton
class WaydataService @Inject()(actorSystem: ActorSystem,
                               val pointRepository: WayPointRepository,
                               val userWayPointsRepository: UserWayPointsRepository)
    extends WaydataMapping {

  implicit val timeout = Timeout(5 seconds)
  implicit val executionContext = actorSystem.dispatcher

  def addPoint(point: Point): Future[Unit] = {
    userWayPointsRepository.insertPoint(
      point.timestamp, point.location.lat, point.location.lon
    )
    pointRepository.save(mapPointToWayPoint(point))
  }

  def report(from: Long, to: Long): Future[Report] = {
    val futureReport: Future[Future[Report]] =
      pointRepository
        .findByInterval(from, to)
        .map(intervalPointsRows => {
            val intervalPoints =
              mapSeqOfRowsToSeqOfPoint(intervalPointsRows)
            val sortedIntervalPoints =
              intervalPoints.sortWith(_.timestamp < _.timestamp)
            val calculation =
              actorSystem.actorOf(Props[CalculationActor])
            (calculation ? CalculationUp(sortedIntervalPoints))
              .mapTo[CalculationResponse]
              .map {
                case CalculationResults(averageSpeed, totalDistance) =>
                  Logger.info(s"CalculationResults: " +
                    s"averageSpeed = $averageSpeed, totalDistance = $totalDistance")
                  Report(averageSpeed, totalDistance, sortedIntervalPoints)
                case CalculationError(message) =>
                  Logger.info(s"CalculationError: $message")
                  Report(Speed(0), Distance(0), sortedIntervalPoints)
                case _ => 
                  Logger.info(s"Undefined message")
                  Report(Speed(0), Distance(0), sortedIntervalPoints)
              }
        })
    futureReport.flatMap(identity)
  }

  def getAll: Future[Seq[Point]] =
    pointRepository.findAll().map(mapSeqOfRowsToSeqOfPoint)

  // TODO - just for education
  def getLastAddedToWay: Future[Option[Point]] =
    pointRepository.findByTimestamp(1000).map(mapWayPointToPointOption)
}
