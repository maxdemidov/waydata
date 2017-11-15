package services.actors

import akka.actor.{ActorRef, PoisonPill}
import akka.pattern.ask
import play.api.Logger
import services.actors.CalculationActor.CalculationMessage
import services.model.{Distance, Section, Segment, Speed}

object SegmentActor {
  case class EvaluateSegment(segment: Segment, calculationActor: ActorRef)
}
class SegmentActor(refTriggeredActor: ActorRef) extends EvaluableActor {

  import CalculationActor._
  import TriggeredActor._
  import SegmentActor._
  import common.Utils

  override def receive: Receive = {

    case EvaluateSegment(segment: Segment, calculationActor: ActorRef) =>
      val seconds = this.seconds(segment)
      val distance = this.distance(segment)
      val speed = distance match {
        case d if d.equals(0) =>
          this.speed(segment)
        case _ =>
          this.speed(distance, seconds)
      }
      val section =
        Section(seconds, Speed(speed), Distance(distance))
      Logger.info(message =
        s"EvaluateSegment, section with distance = $distance and speed = $speed")
      (calculationActor ? SectionResult(section)).mapTo[ResultReceived].map {
        case ResultReceived() =>
          (refTriggeredActor ? UnregisterEvaluable(self)).mapTo[UnregisteredEvaluable].map {
            case UnregisteredEvaluable() =>
              self ! PoisonPill
          }
      }
  }

  def distance(segment: Segment): Double = {
    Utils.kmBetweenEarthCoordinates(
      segment.fromPoint.location.lat,
      segment.fromPoint.location.lon,
      segment.toPoint.location.lat,
      segment.toPoint.location.lon
    )
  }

  def seconds(segment: Segment): Long = {
    segment.toPoint.timestamp - segment.fromPoint.timestamp
  }

  def speed(distance: Double, seconds: Long): Double = {
    val hours =
      seconds.toDouble / SECONDS_IN_HOUR.toDouble
    distance / hours
  }

  def speed(segment: Segment): Double = {
    (segment.toPoint.speed.value + segment.fromPoint.speed.value) / 2
  }
}
