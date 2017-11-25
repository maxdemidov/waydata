package controllers

import javax.inject.Inject

import model.{Way, WaydataSerializations}
import play.api.libs.json.{JsError, Json, Reads}
import play.api.mvc.{Action, AnyContent, BodyParser, Controller}
import services.{WaydataService, WaydataWayService}

import scala.concurrent.{ExecutionContext, Future}

class WaydataWayController @Inject()(waydataWayService: WaydataWayService)
                                    (waydataService: WaydataService)
                                    (implicit exec: ExecutionContext)
  extends Controller with WaydataSerializations  {

  private def validateJson[A: Reads]: BodyParser[A] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  // TODO - fix async and validation
  def addWay(): Action[Way] = Action(validateJson[Way]) { request =>
    val way = request.body
    waydataWayService.addWay(way)
    Ok(
      Json.obj(
        "status" -> "OK",
        "message" -> (
          "Way with name '" + way.name + "' saved.")
      )
    )
  }

  def getAllWays: Action[AnyContent] = Action.async {
    waydataWayService.getAllWays.map(
      ways => Ok(Json.toJson(ways))
    )
  }

  def getWayByUuid(wayUuid: java.util.UUID): Action[AnyContent] = Action.async {
    waydataWayService.getWayByUuid(wayUuid).map {
      case None => NotFound
      case Some(way) => Ok(Json.toJson(way))
    }
  }

  def getAllPointsByWayUuid(wayUuid: java.util.UUID): Action[AnyContent] = Action.async {
    // TODO - continue from here !!
    waydataService.getAll.map(
      points => Ok(Json.toJson(points))
    )
  }

  // TODO - remove way with all points in cassandra
  def removeWay(uuid: java.util.UUID): Action[AnyContent] = Action.async {
    Future {
      NotImplemented
    }
  }

  // TODO - edit name
  def editWay: Action[Way] = Action(validateJson[Way]) { request =>
    val way = request.body
    NotImplemented
  }

  def getWayExample: Action[AnyContent] = Action.async {
    Future {
      Ok(Json.toJson(
        Way(
          new java.util.UUID(0, 0),
          new java.util.UUID(1, 1),
          "example way",
          new java.util.Date()
        )
      ))
    }
  }
}
