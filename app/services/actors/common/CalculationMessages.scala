package services.actors.common

import services.model._
import akka.actor.ActorRef
import services.model.Point
import services.model.Segment
import services.model.Section
import services.model.Speed

object CalculationMessages {
  sealed trait CalculationMessage
  case class CalculationUp(points: List[Point]) extends CalculationMessage
  case class SegmentationPoints(points: List[Point]) extends CalculationMessage
  case class SegmentationDone() extends CalculationMessage
  case class SectionKnock() extends CalculationMessage
  case class EvaluateSegment(segment: Segment, calculationActor: ActorRef) extends CalculationMessage
  case class EvaluateSections(s1: Section, s2: Section, calculationActor: ActorRef) extends CalculationMessage
  case class SectionResult(section: Section) extends CalculationMessage

  sealed trait CalculationResponse
  case class CalculationResults(averageSpeed: Speed, totalDistance: Distance) extends CalculationResponse
}
