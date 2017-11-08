package services.actors

import services.model.{Segment, Point}
import akka.actor.{PoisonPill, Props}
import services.actors.common.ImplicitActor

class SegmentationActor() extends ImplicitActor {

  import common.CalculationMessages._

  override def receive: Receive = {

    case SegmentationPoints(points: List[Point]) =>
      val calculationActor = sender()
      val segments: List[Segment] =
        points.foldLeft((List[Segment](), Option.empty[Point]))(
          (segmentation: (List[Segment], Option[Point]), current: Point) => {
            segmentation._2 match {
              case None =>
                (segmentation._1, Option.apply(current))
              case Some(previous: Point) =>
                // TODO - instantiate SegmentActor actor here
                (Segment(previous, current) :: segmentation._1, Option.apply(current))
            }
          }
        )._1
      segments.foreach(segment => {
        calculationActor ! SectionKnock()
        val pointsEvaluationMessages =
          EvaluateSegment(segment, calculationActor)
        actorSystem.actorOf(Props[SegmentActor]) ! pointsEvaluationMessages
      })
      sender ! SegmentationDone()
      self ! PoisonPill
  }
}
