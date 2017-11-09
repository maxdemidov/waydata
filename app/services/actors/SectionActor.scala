package services.actors

import services.model.{Section, Distance, Speed, Segment}
import akka.actor.{PoisonPill, ActorRef}
import services.actors.common.ImplicitActor

class SectionActor extends ImplicitActor {

  import common.CalculationMessages._

  override def receive: Receive = {

    case EvaluateSections(s1: Section, s2: Section, calculationActor: ActorRef) =>

      val millis = this.millis(s1, s2)
      val distance = this.distance(s1, s2)
      val speed = this.speed(distance, millis)

      val combinedSection =
        Section(millis, Speed(speed), Distance(distance))

      calculationActor ! SectionResult(combinedSection)

      self ! PoisonPill
  }

  def distance(s1: Section, s2: Section) = {
    s1.distance.distance + s2.distance.distance
  }

  def millis(s1: Section, s2: Section) = {
    s1.millis + s2.millis
  }

  def speed(distance: Double, millis: Long) = {
     distance / millis
  }
}
