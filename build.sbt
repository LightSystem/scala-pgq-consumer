lazy val commonSettings = Seq(
  version := "0.1.0",
  scalaVersion := "2.11.7"
)

lazy val root = (project in file(".")).
	settings(commonSettings: _*).
	settings(
	    name := "scala-pgq-consumer"
	)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "anorm" % "2.4.0",
  "com.typesafe.akka" %% "akka-actor" % "2.4.0",
  "commons-codec" % "commons-codec" % "1.10",
  "com.google.guava" % "guava" % "17.0"
)