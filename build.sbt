import play.sbt.PlayScala

name := "waydata"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  filters,
  "org.postgresql" % "postgresql" % "42.0.0",
  "com.nimbusds" % "nimbus-jose-jwt" % "4.34.2",
  "com.typesafe.slick" %% "slick" % "3.2.0",
  "org.flywaydb" %% "flyway-play" % "3.0.1",
  "org.apache.commons" % "commons-email" % "1.4",
  "com.github.tminglei" %% "slick-pg" % "0.15.0-RC",
  "org.scalaz" %% "scalaz-core" % "7.2.10"
)

fork in run := false


