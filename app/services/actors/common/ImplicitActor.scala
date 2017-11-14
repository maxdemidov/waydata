package services.actors.common

import java.text.SimpleDateFormat
import java.util.{Date, SimpleTimeZone}

import akka.actor.Actor
import akka.util.Timeout
import scala.concurrent.duration._
import scala.language.postfixOps

trait ImplicitActor extends Actor {

  implicit val actorSystem = context.system
  implicit val timeout = Timeout(5 seconds)
  implicit val executionContext = actorSystem.dispatcher

  val SECONDS_IN_HOUR: Long = 60 * 60

  val dateFormat: SimpleDateFormat = {
    val dateFormat =
      new SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
    dateFormat.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"))
    dateFormat
  }

  def nowInSimpleFormat(): String = {
    dateFormat.format(new Date())
  }
  def dateInSimpleFormat(timestamp: Long): String = {
    dateFormat.format(new Date(timestamp))
  }
}
