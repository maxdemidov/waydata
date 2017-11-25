package repositories.postgres.persistence

import java.sql.Timestamp

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

case class WaydataUser(userUuid: java.util.UUID,
                       name: String,
                       email: Option[String],
                       phone: Option[String],
                       created: Timestamp)

class WaydataUserTable(tag: Tag) extends Table[WaydataUser](tag, "waydata_user") {

  def userUuid = column[java.util.UUID]("user_uuid", O.PrimaryKey)
  def name = column[String]("user_name")
  def email = column[String]("user_email")
  def phone = column[String]("user_phone")
  def created = column[Timestamp]("user_created")

  def * : ProvenShape[WaydataUser] =
    (userUuid, name, email.?, phone.?, created) <> (WaydataUser.tupled, WaydataUser.unapply)
}