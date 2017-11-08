package services.actors.common

import akka.actor.Actor
import akka.util.Timeout
import scala.concurrent.duration._
import scala.language.postfixOps

trait ImplicitActor extends Actor {

  implicit val actorSystem = context.system
  implicit val timeout = Timeout(5 seconds)
  implicit val executionContext = actorSystem.dispatcher

  val dateFormat =
    new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm:ss")

  def nowInSimpleFormat(): String = {
    dateFormat.format(new java.util.Date())
  }
  def dateInSimpleFormat(timestamp: Long): String = {
    dateFormat.format(new java.util.Date(timestamp))
  }
}
