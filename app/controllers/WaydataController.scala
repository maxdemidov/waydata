package controllers

import javax.inject.Inject

import play.api.mvc._
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}
import services._
import model._

class WaydataController @Inject()(waydataService: WaydataService)
                                 (implicit exec: ExecutionContext)
    extends Controller with WaydataSerializations {

  private def validateJson[A: Reads]: BodyParser[A] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

//  def addPoint(): Action[Point] = Action.async(parse.json) { request =>
//    request.body.validate[Point].map {
//      point =>
//        waydataService.addPoint(point).map {
//          response =>
//            if (response.status == 200) {
//              response.json.validate[Bar].map {
//                point =>
//                  Ok(
//                    Json.obj(
//                      "status" -> "OK",
//                      "message" -> ("Point with timestamp '" + point.timestamp + "' saved.")
//                    )
//                  )
//              }.recoverTotal { e : JsError =>
//                BadRequest("The JSON in the body is not valid.")
//              }
//            } else {
//              BadRequest("alo")
//            }
//        }.recoverTotal {
//          exception : JsError =>
//            Future.successful(BadRequest("The JSON in the body is not valid."))
//        }
//    }
//  }

  // TODO - fix using exception from future if not success
  def addPoint(): Action[Point] = Action(validateJson[Point]) { request =>
    val point = request.body
    waydataService.addPoint(point)
    Ok(
      Json.obj(
        "status" -> "OK",
        "message" -> ("Point with timestamp '" + point.timestamp + "' saved.")
      )
    )
  }

  def getUserWayPointExample: Action[AnyContent] = Action.async {
    Future {
      Ok(Json.toJson(
        UserWayPoint(
          new java.util.UUID(123, 45),
          new java.util.UUID(678, 90),
          Point(
            new java.util.Date().getTime,
            Speed(0),
            Location(
              45.11.toFloat,
              45.99.toFloat
            )
          )
        )
      ))
    }
  }

  @deprecated // TODO - remove
  def allPoints: Action[AnyContent] = Action.async {
    waydataService.getAll.map(
      points => Ok(Json.toJson(points))
    )
  }
}
