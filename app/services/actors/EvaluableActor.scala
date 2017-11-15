package services.actors

import services.actors.common.ImplicitActor

abstract class EvaluableActor extends ImplicitActor {

  val SECONDS_IN_HOUR: Long = 60 * 60

  def speed(distance: Double, seconds: Long): Double = {
    val hours =
      seconds.toDouble / SECONDS_IN_HOUR.toDouble
    distance / hours
  }
}
