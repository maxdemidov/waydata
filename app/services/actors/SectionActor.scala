package services.actors

import services.model.{Section, Distance, Speed, Segment}
import akka.actor.{PoisonPill, ActorRef}
import services.actors.common.ImplicitActor

class SectionActor extends ImplicitActor {

  import common.CalculationMessages._

  override def receive: Receive = {

    case EvaluateSections(x: Section, y: Section, calculationActor: ActorRef) =>
      // TODO - evaluation new section
      calculationActor ! SectionResult(Section(Speed(2), Distance(2)))
      self ! PoisonPill
  }
}
