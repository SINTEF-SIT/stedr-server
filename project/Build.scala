import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "virtualWalls"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaEbean,
    "com.google.guava" % "guava" % "14.0",
    "com.google.code.gson" % "gson" % "2.2",
    "commons-io" % "commons-io" % "2.3",
    "org.jsoup" % "jsoup" % "1.6.3"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
