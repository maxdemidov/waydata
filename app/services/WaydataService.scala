package services

import javax.inject.{Inject, Singleton}

import akka.util.Timeout
import akka.pattern.ask
import akka.actor.{ActorSystem, Props}
import play.api.Logger
import scala.concurrent.Future
import scala.concurrent.duration._
import services.model.{Distance, Point, Report, Speed}
import services.actors.CalculationActor
import services.actors.CalculationActor.{CalculationResponse, CalculationResults, CalculationUp}
import repositories.WayPointRepository
import services.actors.CalculationActor.CalculationError

@Singleton
class WaydataService @Inject() (actorSystem: ActorSystem,
                                val pointRepository: WayPointRepository)
    extends WaydataMapping {

  implicit val timeout = Timeout(5 seconds)
  implicit val executionContext = actorSystem.dispatcher

  def save(point: Point): Future[Unit] =
    pointRepository.save(mapPointToWayPoint(point))

  // TODO - what happende if timeout for one of section (losing one actor) and as a result for all calculation (knocks > 0)
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
              }
        })
    futureReport.flatMap(identity)
  }

  def getAll: Future[Seq[Point]] =
    pointRepository.findAll().map(mapSeqOfRowsToSeqOfPoint)

  def getExample: Future[Option[Point]] =
    pointRepository.findByTimestamp(1).map(mapWayPointToPointOption)
}
