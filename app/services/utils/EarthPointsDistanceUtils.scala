package services.utils

object EarthPointsDistanceUtils {

  private val earthRadiusKm = 6371

  private def degreesToRadians(degrees: Double): Double = {
    degrees * Math.PI / 180
  }

  def kmBetweenEarthCoordinates(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double = {

    val dLat = degreesToRadians(lat2-lat1)
    val dLon = degreesToRadians(lon2-lon1)

    val dlat1 = degreesToRadians(lat1)
    val dlat2 = degreesToRadians(lat2)

    val a =
      Math.sin(dLat/2) * Math.sin(dLat/2) +
      Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(dlat1) * Math.cos(dlat2)

    val c =
      2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))

    earthRadiusKm * c
  }
}
