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
      (points zip points.tail).map(
        neighbouringPoints => {
          Logger.info(message =
            s"SegmentationPoints, segmentation " +
              s"form point with date = ${dateInSimpleFormat(neighbouringPoints._1.timestamp)} " +
              s"to point with date = ${dateInSimpleFormat(neighbouringPoints._2.timestamp)}")
          val segmentRef =
            actorSystem.actorOf(Props(new SegmentActor(refCountingActor)))
          (refCountingActor ? RegisterEvaluable(segmentRef)).mapTo[RegisteredEvaluable].map {
            case RegisteredEvaluable() =>
              val pointsEvaluationMessage =
                EvaluateSegment(Segment(neighbouringPoints._1, neighbouringPoints._2), refCalculationActor)
              segmentRef ! pointsEvaluationMessage
          }
        }
      )
      refCountingActor ! SegmentationDone()
      self ! PoisonPill
  }
}
