
organization in ThisBuild := "me.alexray"
version in ThisBuild := "0.0.17-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.11"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3" % Test
val kubeClientSide = "me.alexray" %% "lagomkubeclientside" % "0.0.17-SNAPSHOT"


val lagomVersion = "1.3.4"

lazy val telegramBotService_api = (project in file("telegramBotService_api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      kubeClientSide
    )
  )

lazy val telegramBotService_impl = (project in file("telegramBotService_impl"))
  .enablePlugins(LagomScala)
  .enablePlugins(NewRelic)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      kubeClientSide,
      macwire,
      scalaTest,
      "info.mukel" %% "telegrambot4s" % "2.9.5"
    ),
    packageName in Docker := "telegrambot",
    dockerRepository := Some("docker.io/YOUR_DOCKER_REPOSITORY"),
    newrelicVersion := "3.39.1"
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(telegramBotService_api, wolframService_api)

lazy val wolframService_api = (project in file("wolframService_api"))
  .settings(
    libraryDependencies ++= Seq(
      kubeClientSide,
      lagomScaladslApi
    )
  )

lazy val wolframService_impl = (project in file("wolframService_impl"))
  .enablePlugins(LagomScala)
  .enablePlugins(NewRelic)
  .settings(
    libraryDependencies ++= Seq(
      kubeClientSide,
      lagomScaladslTestKit,
      macwire,
      scalaTest,
      "com.typesafe.akka" % "akka-http_2.11" % "10.0.7"
    ),
    packageName in Docker := "wolframservice",
    //version in Docker := "0.0.4",
    dockerRepository := Some("docker.io/YOUR_DOCKER_REPOSITORY"),
    newrelicVersion := "3.39.1"
  )
  .dependsOn(wolframService_api)


lazy val helloWorldService_api = (project in file("helloWorldService_api"))
  .settings(
    libraryDependencies ++= Seq(
      kubeClientSide,
      lagomScaladslApi
    )
  )

lazy val helloWorldService_impl = (project in file("helloWorldService_impl"))
  .enablePlugins(LagomScala)
  .enablePlugins(NewRelic)
  .settings(
    libraryDependencies ++= Seq(
      kubeClientSide,
      lagomScaladslTestKit,
      macwire,
      scalaTest,
      "com.typesafe.akka" % "akka-http_2.11" % "10.0.7"

    ),
    packageName in Docker := "helloworldservice",
    dockerRepository := Some("docker.io/YOUR_DOCKER_REPOSITORY"),
    newrelicVersion := "3.39.1"
  )
  .dependsOn(helloWorldService_api)


lazy val telegramWolframBot = (project in file("."))
  .aggregate(
    telegramBotService_api, telegramBotService_impl,
    wolframService_api, wolframService_impl,
    helloWorldService_api, helloWorldService_impl
  )

// disable persistence (Cassandra)
lagomCassandraEnabled in ThisBuild := false
// do not delete database files on start
lagomCassandraCleanOnStart in ThisBuild := false
// disable message broker (Kafka)
lagomKafkaEnabled in ThisBuild := false
// disable service locator broker (Kafka)
lagomServiceLocatorEnabled in ThisBuild := false


val setEnvTask = TaskKey[Unit]("setEnvTask", "sets development environment variables")
setEnvTask := {
  System.setProperty("WOLFRAM_APPID", "") // i.e. 1234
  System.setProperty("TELEGRAM_BOT_TOKEN", "") // i.e. 1234
  System.setProperty("WOLFRAM_SERVICE_HOST", "localhost")
  System.setProperty("TELEGRAM_SERVICE_HOST", "localhost")
  System.setProperty("SERVICE_LOCATOR_ADDRESS", "http://localhost:8000")
  System.setProperty("SERVICE_INSTANCE_SUFFIX", "1")
  System.setProperty("SERVICE_HOST", "127.0.0.1")
}


runAll := {
  setEnvTask.value
  runAll.value
}