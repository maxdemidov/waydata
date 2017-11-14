package services.actors

import akka.actor.{PoisonPill, ActorRef}
import services.actors.common.ImplicitActor
import services.model.{Section, Speed, Distance, Segment}

class SegmentActor() extends ImplicitActor {

  import common.CalculationMessages._
  import common.Utils

  override def receive: Receive = {

    case EvaluateSegment(segment: Segment, calculationActor: ActorRef) =>

      val millis = this.millis(segment)
      val distance =  this.distance(segment)
      val speed = this.speed(distance, millis)
      // TODO - deside which case of calculation speed is better to use, by time or as average between inputed values
//      val speed = Speed(this.speed(segment))

      val section =
        Section(millis, Speed(speed), Distance(distance))

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

  def speed(distance: Double, millis: Long) = {
    distance / millis
  }
//  def speed(segment: Segment) = {
//    (segment.toPoint.speed.value + segment.fromPoint.speed.value) / 2
//  }
}
