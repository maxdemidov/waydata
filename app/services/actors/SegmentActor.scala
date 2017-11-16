package services.actors

import akka.actor.{ActorRef, PoisonPill}
import akka.pattern.ask
import play.api.Logger
import services.model.{Distance, Section, Segment, Speed}

object SegmentActor {
  case class EvaluateSegment(segment: Segment, calculationActor: ActorRef)
}
class SegmentActor(refCountingActor: ActorRef) extends EvaluableActor {

  import CalculationActor._
  import CountingActor._
  import SegmentActor._
  import EvaluableActor._

  override def receive: Receive = {

    case EvaluateSegment(segment: Segment, calculationActor: ActorRef) =>
      val millis = this.millis(segment)
      val distance = this.distance(segment)
      val speed = distance match {
        case d if d.equals(0) =>
          this.speed(segment)
        case _ =>
          this.speed(distance, millis)
      }
      val section =
        Section(millis, Speed(speed), Distance(distance))
      Logger.info(message =
        s"EvaluateSegment, section with distance = $distance and speed = $speed")
      (calculationActor ? CalculatedSection(section)).mapTo[ResultReceived].map {
        case ResultReceived() =>
          (refCountingActor ? UnregisterEvaluable(self)).mapTo[UnregisteredEvaluable].map {
            case UnregisteredEvaluable() =>
              self ! PoisonPill
          }
      }
  }

  def distance(segment: Segment): Double = {
    import common.Utils
    Utils.kmBetweenEarthCoordinates(
      segment.fromPoint.location.lat,
      segment.fromPoint.location.lon,
      segment.toPoint.location.lat,
      segment.toPoint.location.lon
    )
  }

  def millis(segment: Segment): Long = {
    segment.toPoint.timestamp - segment.fromPoint.timestamp
  }

  def speed(segment: Segment): Double = {
    (segment.toPoint.speed.value + segment.fromPoint.speed.value) / 2
  }
}
