package services

import java.util.Date
import javax.inject.{Inject, Singleton}

import services.model._
import akka.pattern.ask
import akka.actor.{ActorSystem, Props}
import services.actors.CalculationActor
import services.actors.common.CalculationMessages.{CalculationResponse, CalculationResults, CalculationUp}

import scala.concurrent.Future
import akka.util.Timeout
import repositories.{WayPointRepository}
import play.api.Logger

import scala.concurrent.duration._

@Singleton
class WaydataService @Inject() (actorSystem: ActorSystem,
                                val pointRepository: WayPointRepository)
    extends WaydataMapping {

  implicit val timeout = Timeout(5 seconds)
  implicit val executionContext = actorSystem.dispatcher

  def save(point: Point): Future[Unit] =
    pointRepository.save(mapPointToWayPoint(point))

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
                case calculationResults: CalculationResults =>
                  Logger.info(s"Result: " +
                    s"averageSpeed = ${calculationResults.averageSpeed}, " +
                    s"totalDistance = ${calculationResults.totalDistance}")
                  Report(
                    calculationResults.averageSpeed,
                    calculationResults.totalDistance,
                    sortedIntervalPoints
                  )
                case _ =>
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
