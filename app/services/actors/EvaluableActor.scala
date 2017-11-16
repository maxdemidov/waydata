package services.actors

import services.actors.common.ImplicitActor

object EvaluableActor {
  case class ResultReceived()
}
abstract class EvaluableActor extends ImplicitActor {

  val MILLIS_IN_HOUR: Long = 60 * 60 * 1000

  def speed(distance: Double, millis: Long): Double = {
    val hours =
      millis.toDouble / MILLIS_IN_HOUR.toDouble
    distance / hours
  }
}
