package services.actors.common

import akka.actor.ActorRef
import services.model.{Point, Section, Segment, Speed, Distance}

object CalculationMessages {
  sealed trait CalculationMessage
  case class CalculationUp(points: Seq[Point]) extends CalculationMessage
  case class SegmentationPoints(points: Seq[Point]) extends CalculationMessage
  case class EvaluateSegment(segment: Segment, calculationActor: ActorRef) extends CalculationMessage
  case class EvaluateSections(s1: Section, s2: Section, calculationActor: ActorRef) extends CalculationMessage
  case class SectionResult(section: Section) extends CalculationMessage

  sealed trait CalculationResponse
  case class CalculationResults(averageSpeed: Speed, totalDistance: Distance) extends CalculationResponse
}
