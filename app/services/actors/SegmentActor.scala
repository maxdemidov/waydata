package services.actors

import services.model.{Section, Speed, Distance, Segment}
import akka.actor.{PoisonPill, ActorRef}
import services.actors.common.ImplicitActor

class SegmentActor() extends ImplicitActor {

  import common.CalculationMessages._
  import common.Utils

  override def receive: Receive = {

    case EvaluateSegment(segment: Segment, calculationActor: ActorRef) =>

      val millis = this.millis(segment)
      val speed = Speed(this.speed(segment))
      val distance =  Distance(this.distance(segment))

      val section =
        Section(millis, speed, distance)

      calculationActor ! SectionResult(section)
      self ! PoisonPill
  }

  def distance(segment: Segment) = {
    Utils.kmBetweenEarthCoordinates(
      segment.fromPoint.location.lat,
      segment.fromPoint.location.lon,
      segment.toPoint.location.lat,
      segment.toPoint.location.lon
    )
  }

  def millis(segment: Segment) = {
    segment.toPoint.timestamp - segment.fromPoint.timestamp
  }

  def speed(segment: Segment) = {
    (segment.toPoint.speed.value + segment.fromPoint.speed.value) / 2
  }
}
