package services.actors

import services.model.{Segment, Point}
import akka.actor.{PoisonPill, Props}
import services.actors.common.ImplicitActor

class SegmentationActor() extends ImplicitActor {

  import common.CalculationMessages._

  override def receive: Receive = {

    case SegmentationPoints(points: Seq[Point]) =>
      val calculationActor = sender()
      points.foldLeft(Option.empty[Point])(
        (previous: Option[Point], current: Point) => {
          previous match {
            case None =>
              Option.apply(current)
            case Some(previous: Point) =>
              calculationActor ! SectionKnock()
              val pointsEvaluationMessage =
                EvaluateSegment(Segment(previous, current), calculationActor)
              actorSystem.actorOf(Props[SegmentActor]) ! pointsEvaluationMessage
              Option.apply(current)
          }
        }
      )
      sender ! SegmentationDone()
      self ! PoisonPill
  }
}
