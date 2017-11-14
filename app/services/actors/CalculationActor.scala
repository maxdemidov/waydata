package services.actors

import akka.actor.{PoisonPill, ActorRef, Props}
import play.api.Logger
import services.actors.common.ImplicitActor
import services.model.{Point, Section, Speed, Distance}

class CalculationActor() extends ImplicitActor {

  import common.CalculationMessages._

  var refCalculationActor: ActorRef = null
  var refSegmentationActor: ActorRef = null
  var isSegmentationDone = false
  var knocks: Long = 0
  var inMemorySection: Option[Section] = Option.empty[Section]

  override def receive: Receive = {

    // TODO work with special cases more precisely
    // TODO calculate edges as an additional points like they were existed on the FROM and TO places, it's give us a little bit more accuracy

    case CalculationUp(points: Seq[Point]) =>
      Logger.info(message =
        s"CalculationUp, points count = ${points.size}")
      refCalculationActor = sender()
      points.size match {
        case 0 =>
          sender ! CalculationResults(Speed(0), Distance(0))
        case 1 =>
          sender ! CalculationResults(points.head.speed, Distance(0))
        case _ =>
          knocks = points.size - 1
          // TODO - !ERROR! - set knocks here as points.size - 1 becouse SectionResult can possible be earlier then SectionKnock
          refSegmentationActor = actorSystem.actorOf(Props[SegmentationActor])
          refSegmentationActor ! SegmentationPoints(points)
      }
/*
    case SectionKnock() =>
      Logger.info(message =
        s"SectionKnock, knocks + 1, count = ${knocks + 1}")
      knocks = knocks + 1

    case SegmentationDone() =>
      Logger.info(message =
        s"SegmentationDone, knocks, count = $knocks")
      isSegmentationDone = true
*/
    case SectionResult(section: Section) =>
      val calculationActor = self
      Logger.info(message =
        s"SectionResult, knocks - 1, count = ${knocks - 1}, " +
          s"section with distance = ${section.distance.value} and speed = ${section.speed.value}")
      knocks = knocks - 1
      inMemorySection = inMemorySection match {
        case None =>
          if (/*isSegmentationDone &&*/ knocks == 0) {
            refCalculationActor ! CalculationResults(section.speed, section.distance)
            refSegmentationActor ! PoisonPill
            refCalculationActor ! PoisonPill
          }
          Option.apply(section)
        case Some(memorySection: Section) =>
          knocks = knocks + 1
//          calculationActor ! SectionKnock()
          val coupleEvaluationMessage =
            EvaluateSections(memorySection, section, calculationActor)
          actorSystem.actorOf(Props[SectionActor]) ! coupleEvaluationMessage
          Option.empty[Section]
      }
  }
}
