package services.actors

import services.model.{Section, Speed, Distance, Segment}
import akka.actor.{PoisonPill, ActorRef}
import services.actors.common.ImplicitActor

class SegmentActor() extends ImplicitActor {

  import common.CalculationMessages._

  override def receive: Receive = {

    case EvaluateSegment(segment: Segment, calculationActor: ActorRef) =>
      // TODO - evaluation distance between two point and take average speed
      calculationActor ! SectionResult(Section(Speed(1), Distance(1)))
      self ! PoisonPill
  }
}
