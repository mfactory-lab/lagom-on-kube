organization in ThisBuild := "me.alexray"
version in ThisBuild := "0.0.17-SNAPSHOT"
name := "lagomKubeClientSide"

scalaVersion in ThisBuild := "2.11.11"

//lazy val lagomServiceLocator =
//  ProjectRef(uri("https://github.com/lagom/lagom.git#1.3.4"), "devmode-scaladsl")

lazy val lagomKubeClientSide = (project in file("."))
  .settings(
    libraryDependencies ++= Seq(
      "com.lightbend.lagom" % "lagom-scaladsl-dev-mode_2.11" % "1.3.4"
    )
  )
//  .dependsOn(lagomServiceLocator)