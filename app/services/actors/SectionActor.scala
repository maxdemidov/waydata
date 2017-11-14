package services.actors

import akka.actor.{ActorRef, PoisonPill}
import services.actors.common.ImplicitActor
import services.model.{Distance, Section, Speed}

class SectionActor extends ImplicitActor {

  import common.CalculationMessages._

  override def receive: Receive = {

    case EvaluateSections(s1: Section, s2: Section, calculationActor: ActorRef) =>
      val seconds = this.seconds(s1, s2)
      val distance = this.distance(s1, s2)
      val speed = distance match {
        case d if d.equals(0) =>
          this.speed(s1, s2)
        case _ =>
          this.speed(distance, seconds)
      }
      val combinedSection =
        Section(seconds, Speed(speed), Distance(distance))
      calculationActor ! SectionResult(combinedSection)
      self ! PoisonPill
  }

  def distance(s1: Section, s2: Section): Double = {
    s1.distance.value + s2.distance.value
  }

  def seconds(s1: Section, s2: Section): Long = {
    s1.seconds + s2.seconds
  }

  def speed(distance: Double, seconds: Long): Double = {
    val hours =
      seconds.toDouble / SECONDS_IN_HOUR.toDouble
    distance / hours
  }

  def speed(s1: Section, s2: Section): Double = {
    (s1.speed.value + s2.speed.value) / 2
  }
}
