package services.actors

import akka.actor.{ActorRef, PoisonPill}
import akka.pattern.ask
import play.api.Logger
import model.{Distance, Section, Speed}

object SectionActor {
  case class EvaluateSections(s1: Section, s2: Section, calculationActor: ActorRef)
}
class SectionActor(refCountingActor: ActorRef) extends EvaluableActor {

  import CalculationActor._
  import CountingActor._
  import SectionActor._
  import EvaluableActor._

  override def receive: Receive = {

    case EvaluateSections(s1: Section, s2: Section, calculationActor: ActorRef) =>
      val millis = this.millis(s1, s2)
      val distance = this.distance(s1, s2)
      val speed = distance match {
        case d if d.equals(0) =>
          this.speed(s1, s2)
        case _ =>
          this.speed(distance, millis)
      }
      val combinedSection =
        Section(millis, Speed(speed), Distance(distance))
      Logger.info(message =
        s"EvaluateSections, section with distance = $distance and speed = $speed")
      (calculationActor ? CalculatedSection(combinedSection)).mapTo[ResultReceived].map {
        case ResultReceived() =>
          (refCountingActor ? UnregisterEvaluable(self)).mapTo[UnregisteredEvaluable].map {
            case UnregisteredEvaluable() =>
              self ! PoisonPill
          }
      }
  }

  def distance(s1: Section, s2: Section): Double = {
    s1.distance.value + s2.distance.value
  }

  def millis(s1: Section, s2: Section): Long = {
    s1.millis + s2.millis
  }

  def speed(s1: Section, s2: Section): Double = {
    (s1.speed.value + s2.speed.value) / 2
  }
}
