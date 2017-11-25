package repositories.cassandra

import java.lang
import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import com.datastax.driver.core.{Cluster, PreparedStatement, Session}

import scala.concurrent.ExecutionContext

@Singleton()
class UserWayPointsRepository @Inject()(actorSystem: ActorSystem,
                                        implicit val ec: ExecutionContext) {

    val cluster: Cluster =
        Cluster.builder().addContactPoint("127.0.0.1").build()

    val session: Session =
        cluster.connect("waydata")

    val preparedStatement: PreparedStatement =
        session.prepare(
            "INSERT INTO " +
              "user_way_points(user_uuid, way_uuid, created_on, lat, lon) " +
              "VALUES (?, ?, ?, ?, ?)"
        )

    def insertPoint(timestamp: Long, lat: Float, lon: Float): Unit = {

        session.executeAsync(
            preparedStatement.bind(
                new java.util.UUID(1, 1),
                new java.util.UUID(1, 1),
                new java.util.Date(timestamp),
                new java.lang.Float(lat),
                new java.lang.Float(lon)
            )
        )
    }
}
