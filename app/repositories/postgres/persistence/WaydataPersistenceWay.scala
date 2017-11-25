package repositories.postgres.persistence

import java.sql.Timestamp

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

case class WaydataWay(wayUuid: java.util.UUID,
                      userUuid: java.util.UUID,
                      name: String,
                      created: Timestamp)

class WaydataWayTable(tag: Tag) extends Table[WaydataWay](tag, "waydata_way") {

  def wayUuid = column[java.util.UUID]("way_uuid", O.PrimaryKey)
  def userUuid = column[java.util.UUID]("user_uuid")
  def name = column[String]("way_name")
  def created = column[Timestamp]("way_created")

  def * : ProvenShape[WaydataWay] =
    (wayUuid, userUuid, name, created) <> (WaydataWay.tupled, WaydataWay.unapply)
}