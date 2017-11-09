package services.actors

import services.model._
import akka.actor.{PoisonPill, ActorRef, Props}
import services.model.Distance
import services.model.Point
import services.model.Speed
import services.actors.common.ImplicitActor
import play.api.Logger

class CalculationActor() extends ImplicitActor {

  import common.CalculationMessages._

  var ref: ActorRef = null
  var isSegmentationDone = false
  var knocks: Long = 0
  var inMemorySection: Option[Section] = Option.empty[Section]

  override def receive: Receive = {

    // TODO work with special cases more precisely
    // TODO calculate edges as an additional points like they were existed on the FROM and TO places, it's give us a little bit more accuracy

    case CalculationUp(points: List[Point]) =>
      ref = sender()
      points.size match {
        case 0 =>
          sender ! CalculationResults(Speed(0), Distance(0))
        case 1 =>
          sender ! CalculationResults(points.head.speed, Distance(0))
        case _ =>
          actorSystem.actorOf(Props[SegmentationActor]) ! SegmentationPoints(points)
      }

    case SectionKnock() =>
      Logger.info(message = "knocks count = "+(knocks + 1))
      knocks = knocks + 1

    case SegmentationDone() =>
      isSegmentationDone = true

    case SectionResult(section: Section) =>
      Logger.info(message = "section with distance = " + section.distance.value)
      val calculationActor = self
      knocks = knocks - 1
      inMemorySection = inMemorySection match {
        case None =>
          if (isSegmentationDone && knocks == 0) {
            ref ! CalculationResults(section.speed, section.distance)
            ref ! PoisonPill
          }
          Option.apply(section)
        case Some(memorySection: Section) =>
          calculationActor ! SectionKnock()
          val coupleEvaluationMessage =
            EvaluateSections(memorySection, section, calculationActor)
          actorSystem.actorOf(Props[SectionActor]) ! coupleEvaluationMessage
          Option.empty[Section]
      }
  }
}
