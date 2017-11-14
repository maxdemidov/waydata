package services.actors

import akka.actor.{PoisonPill, Props}
import play.api.Logger
import services.actors.common.ImplicitActor
import services.model.{Point, Segment}

class SegmentationActor() extends ImplicitActor {

  import common.CalculationMessages._

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
              //calculationActor ! SectionKnock()
              val pointsEvaluationMessage =
                EvaluateSegment(Segment(previous, current), calculationActor)
              actorSystem.actorOf(Props[SegmentActor]) ! pointsEvaluationMessage
              Option.apply(current)
          }
        }
      )
      //sender ! SegmentationDone()
      self ! PoisonPill
  }
}
