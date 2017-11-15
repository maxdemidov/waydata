package services.actors

import akka.actor.{ActorRef, PoisonPill, Props}
import akka.pattern.ask
import play.api.Logger
import services.actors.common.ImplicitActor
import services.model.{Point, Segment}

object SegmentationActor {
  case class SegmentationPoints(points: Seq[Point])
}
class SegmentationActor(refCalculationActor: ActorRef,
                        refCountingActor: ActorRef) extends ImplicitActor {

  import CountingActor._
  import SegmentActor._
  import SegmentationActor._

  override def receive: Receive = {

    case SegmentationPoints(points: Seq[Point]) =>
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
                actorSystem.actorOf(Props(new SegmentActor(refCountingActor)))
              (refCountingActor ? RegisterEvaluable(segmentRef)).mapTo[RegisteredEvaluable].map {
                case RegisteredEvaluable() =>
                  val pointsEvaluationMessage =
                    EvaluateSegment(Segment(previous, current), refCalculationActor)
                  segmentRef ! pointsEvaluationMessage
              }
              Option.apply(current)
          }
        }
      )
      refCountingActor ! SegmentationDone()
      self ! PoisonPill
  }
}
