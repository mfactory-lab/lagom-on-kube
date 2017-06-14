organization in ThisBuild := "me.alexray"
version in ThisBuild := "0.0.17-SNAPSHOT"
name := "lagomKubeServiceRegister"

scalaVersion in ThisBuild := "2.11.11"

//lazy val lagomServiceLocator =
//  ProjectRef(uri("https://github.com/lagom/lagom.git#1.3.3"), "service-locator")

lazy val lagomKubeServiceRegister = (project in file("."))
  .settings(
    libraryDependencies ++= Seq(
      "com.lightbend.lagom" %% "lagom-service-locator" % "1.3.4",
      "com.github.romix.akka" %% "akka-kryo-serialization" % "0.5.0",
      "com.typesafe.akka"          %% "akka-persistence" % "2.5.2",
      "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.54"
    ),
    packageName in Docker := "lagomkubeserviceregister",
    dockerRepository := Some("docker.io/YOUR_DOCKER_REPOSITORY"),
    newrelicVersion := "3.39.1"
  )
//  .dependsOn(lagomServiceLocator)
  .enablePlugins(JavaServerAppPackaging)
  .enablePlugins(UniversalPlugin)
  .enablePlugins(NewRelic)

val setEnvTask = TaskKey[Unit]("setEnvTask", "sets development environment variables")
setEnvTask := {
  System.setProperty("CASSANDRA_CONNECTION_IP", "127.0.0.1")
}

run in Compile := {
  setEnvTask.value
  (run in Compile).evaluated
}
