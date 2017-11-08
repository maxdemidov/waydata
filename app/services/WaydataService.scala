package services

import javax.inject.{Inject, Singleton}
import services.model._
import akka.pattern.ask
import akka.actor.{Props, ActorSystem}
import services.actors.CalculationActor
import services.actors.common.CalculationMessages.{CalculationResults, CalculationResponse, CalculationUp}
import scala.concurrent.Future
import akka.util.Timeout
import scala.concurrent.duration._

@Singleton
class WaydataService @Inject() (actorSystem: ActorSystem) {

  implicit val timeout = Timeout(5 seconds)
  implicit val executionContext = actorSystem.dispatcher

  def getAll: List[Point] =
    WaydataPointRepository.list

  def getExample:Point =
    WaydataPointRepository.example

  def save(point: Point) =
    WaydataPointRepository.save(point)

  def report(from: Long, to: Long): Future[Report] = {

    val selectedPoints: List[Point] =
      this.getAll.filter(
        point =>
          point.timestamp >= from && point.timestamp <= to
      )

    val selectedSortedPoints =
      selectedPoints.sortWith(_.timestamp < _.timestamp)

    val calculation =
      actorSystem.actorOf(Props[CalculationActor])

    (calculation ? CalculationUp(selectedSortedPoints)).mapTo[CalculationResponse].map {
      case calculationResults: CalculationResults =>
        Report(
          calculationResults.averageSpeed,
          calculationResults.totalDistance,
          selectedSortedPoints
        )
      case _ =>
        // TODO: decide what return
        Report(
          Speed(0), Distance(0), Nil
        )
    }
  }
}
