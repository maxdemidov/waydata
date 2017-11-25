package controllers

import javax.inject.Inject

import play.api.libs.json.{JsError, Json, Reads}
import play.api.mvc.{Action, AnyContent, BodyParser, Controller}
import services.{WaydataUserService, WaydataWayService}
import model.{User, WaydataSerializations}

import scala.concurrent.{ExecutionContext, Future}

class WaydataUserController @Inject()(waydataUserService: WaydataUserService)
                                     (waydataWayService: WaydataWayService)
                                     (implicit exec: ExecutionContext)
    extends Controller with WaydataSerializations  {

  private def validateJson[A: Reads]: BodyParser[A] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  // TODO - fix async and validation
  def addUser(): Action[User] = Action(validateJson[User]) { request =>
    val user = request.body
    waydataUserService.addUser(user)
    Ok(
      Json.obj(
        "status" -> "OK",
        "message" -> (
          "User with email '" + user.email + "' and phone '" + user.phone + "'saved.")
      )
    )
  }

  def getAllUsers: Action[AnyContent] = Action.async {
    waydataUserService.getAllUsers.map(
      users => Ok(Json.toJson(users))
    )
  }

  def getUserByUuid(userUuid: java.util.UUID): Action[AnyContent] = Action.async {
    waydataUserService.getUserByUuid(userUuid).map {
      case None => NotFound
      case Some(user) => Ok(Json.toJson(user))
    }
  }

  def getAllWaysByUserUuid(userUuid: java.util.UUID): Action[AnyContent] = Action.async {
    waydataWayService.getAllWaysByUserUuid(userUuid).map(
      ways => Ok(Json.toJson(ways))
    )
  }

  // TODO - remove user with all ways in postgres and cassandra
  def removeUser(uuid: java.util.UUID): Action[AnyContent] = Action.async {
    Future {
      NotImplemented
    }
  }

  // TODO - edit name
  def editUser: Action[User] = Action(validateJson[User]) { request =>
    val user = request.body
    NotImplemented
  }

  def getUserExample: Action[AnyContent] = Action.async {
    Future {
      Ok(Json.toJson(
        User(
          new java.util.UUID(0, 0),
          "example user",
          Some("my@mail.it"),
          Some("089-123-45-67"),
          new java.util.Date()
        )
      ))
    }
  }
}
