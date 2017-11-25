package controllers

import javax.inject.Inject

import model.WaydataSerializations
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller}
import services.WaydataService

import scala.concurrent.ExecutionContext

class WaydataReportController @Inject()(waydataService: WaydataService)
                                       (implicit exec: ExecutionContext)
  extends Controller with WaydataSerializations {

  def report(from: Long, to: Long): Action[AnyContent] = Action.async {
    waydataService.report(from, to).map(
      report => Ok(Json.toJson(report))
    )
  }

  // TODO - get all points / ways / users in square built for pair of (lat,lon)

  // TODO - get all points / ways / users in (lat,lon)+radius

}
