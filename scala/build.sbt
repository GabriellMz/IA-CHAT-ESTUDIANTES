name := "EVEA-Bloque1"
version := "0.1"
scalaVersion := "2.13.12"

val akkaHttpVersion = "10.5.0"
val akkaVersion = "2.8.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  
  // Conexión a Sql Workbench 
  "com.typesafe.slick" %% "slick" % "3.4.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.4.1",
  "mysql" % "mysql-connector-java" % "8.0.33"
  
  // Seguridad JWT
  "com.github.jwt-scala" %% "jwt-core" % "9.4.4"
)