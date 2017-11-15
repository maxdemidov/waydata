package services.actors

import akka.actor.{ActorRef, PoisonPill, Props}
import akka.pattern.ask
import play.api.Logger
import services.actors.common.ImplicitActor
import services.model.{Point, Segment}

object SegmentationActor {
  case class SegmentationDone()
}
class SegmentationActor(refTriggeredActor: ActorRef) extends ImplicitActor {

  import CalculationActor._
  import TriggeredActor._
  import SegmentationActor._

  override def receive: Receive = {

    case SegmentationPoints(points: Seq[Point]) =>
      val calculationActor = sender()
      points.foldLeft(Option.empty[Point])(
        (previous: Option[Point], current: Point) => {
          Logger.info(message =
            s"SegmentationPoints, segmentation point with date: ${current.timestamp}")
//          Logger.info(message =
//            s"SegmentationPoints, segmentation point with date: ${dateInSimpleFormat(current.timestamp)}")
          previous match {
            case None =>
              Option.apply(current)
            case Some(previous: Point) =>
              val segmentRef =
                actorSystem.actorOf(Props(new SegmentActor(refTriggeredActor)))
              (refTriggeredActor ? RegisterEvaluable(segmentRef)).mapTo[RegisteredEvaluable].map {
                case RegisteredEvaluable() =>
                  val pointsEvaluationMessage =
                    EvaluateSegment(Segment(previous, current), calculationActor)
                  segmentRef ! pointsEvaluationMessage
              }
              Option.apply(current)
          }
        }
      )
      refTriggeredActor ! SegmentationDone()
  }
}
