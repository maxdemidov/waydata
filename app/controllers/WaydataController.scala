package controllers

import play.api.mvc._
import play.api.libs.json._
import akka.actor.ActorSystem
import javax.inject.Inject
import scala.concurrent.ExecutionContext

import services._
import services.model._

class WaydataController @Inject() (actorSystem: ActorSystem)
                                  (waydataService: WaydataService)
                                  (implicit exec: ExecutionContext)
  extends Controller with WaydataSerializations {

  def validateJson[A : Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  def point = Action(validateJson[Point]) { request =>
      val point = request.body
      waydataService.save(point)
      Ok(
        Json.obj(
          "status" -> "OK",
          "message" -> ("Point with timestamp '"+point.timestamp+"' saved.")
        )
      )
  }

  def report(from: Long, to: Long) = Action.async {
    waydataService.report(from, to).map(
      report => Ok(Json.toJson(report))
    )
  }

  def example = Action {
    val point = waydataService.getExample
    Ok(Json.toJson(point))
  }

  def all = Action {
    val points = waydataService.getAll
    Ok(Json.toJson(points))
  }
}
