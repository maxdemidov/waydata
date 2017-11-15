package services.actors

import akka.actor.{ActorPath, ActorRef, Kill, PoisonPill}
import play.api.Logger
import services.actors.common.ImplicitActor

object TriggeredActor {
  case class RegisterEvaluable(segmentRef: ActorRef)
  case class RegisteredEvaluable()
  case class UnregisterEvaluable(segmentRef: ActorRef)
  case class UnregisteredEvaluable()
  case class SegmentationDone()
}
class TriggeredActor(refCalculationActor: ActorRef) extends ImplicitActor {

  var evaluations: Map[ActorPath, ActorRef] = Map()
  var isSegmentationDone = false

  import CalculationActor._
  import TriggeredActor._

  override def receive: Receive = {

    case RegisterEvaluable(evaluableRef) =>
      evaluations = evaluations + (evaluableRef.path -> evaluableRef)
      Logger.info(message =
        s"RegisterEvaluable, evaluations.size = ${evaluations.size}, with: ${evaluableRef.path.name}")
      sender ! RegisteredEvaluable()

    case UnregisterEvaluable(evaluableRef) =>
      evaluations = evaluations - evaluableRef.path
      Logger.info(message =
        s"UnregisterEvaluable, evaluations.size = ${evaluations.size}, with: ${evaluableRef.path.name}")
      sender ! UnregisteredEvaluable()
      if (isSegmentationDone) {
        evaluations.size match {
          case 0 =>
            refCalculationActor ! CalculationDone()
            self ! PoisonPill
          case _ =>
        }
      }

    case SegmentationDone() =>
      Logger.info(message = s"SegmentationDone")
      isSegmentationDone = true
  }
}
