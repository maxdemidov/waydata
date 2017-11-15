package services.actors

import akka.actor.{ActorRef, PoisonPill, Props}
import akka.pattern.ask
import play.api.Logger
import services.actors.common.ImplicitActor
import services.model.{Distance, Point, Section, Speed}

object CalculationActor {
  case class CalculationDone()
  case class ResultReceived()
}
class CalculationActor() extends ImplicitActor {

  import common.CalculationMessages._
  import CalculationActor._
  import TriggeredActor._

  var refSender: ActorRef = _

  var refCalculationActor: ActorRef = self
  var refTriggeredActor: ActorRef = _
  var refSegmentationActor: ActorRef = _

  var inMemorySection: Option[Section] = Option.empty[Section]

  override def receive: Receive = receiveSegmentation

  def receiveSegmentation: Receive = {

    // TODO work with special cases more precisely
    // TODO calculate edges as an additional points like they were existed on the FROM and TO places, it's give us a little bit more accuracy

    case CalculationUp(points: Seq[Point]) =>
      Logger.info(message =
        s"CalculationUp, points count = ${points.size}")
      refSender = sender()
      points.size match {
        case 0 =>
          sender ! CalculationResults(Speed(0), Distance(0))
        case 1 =>
          sender ! CalculationResults(points.head.speed, Distance(0))
        case _ =>
          refTriggeredActor =
            actorSystem.actorOf(Props(new TriggeredActor(refCalculationActor)))
          refSegmentationActor =
            actorSystem.actorOf(Props(new SegmentationActor(refTriggeredActor)))
          context become receiveCalculation
          refSegmentationActor ! SegmentationPoints(points)
      }
  }

  def receiveCalculation: Receive = {

    case SectionResult(section: Section) =>
      Logger.info(message =
        s"SectionResult, " +
          s"section with distance = ${section.distance.value} " +
          s"and speed = ${section.speed.value}")
      inMemorySection = inMemorySection match {
        case None =>
          Option.apply(section)
        case Some(memorySection: Section) =>
          val sectionRef =
            actorSystem.actorOf(Props(new SectionActor(refTriggeredActor)))
          (refTriggeredActor ? RegisterEvaluable(sectionRef)).mapTo[RegisteredEvaluable].map {
            case RegisteredEvaluable() =>
              val coupleEvaluationMessage =
                EvaluateSections(memorySection, section, refCalculationActor)
              sectionRef ! coupleEvaluationMessage
          }
          Option.empty[Section]
      }
      sender ! ResultReceived()

    case CalculationDone() =>
      Logger.info(message = s"CalculationDone")
      inMemorySection match {
        case None =>
          // TODO exception
        case Some(memorySection: Section) =>
          refSender ! CalculationResults(memorySection.speed, memorySection.distance)
      }
      refSegmentationActor ! PoisonPill
      refTriggeredActor ! PoisonPill
      refCalculationActor ! PoisonPill
  }
}
