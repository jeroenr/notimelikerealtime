import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "realtimepermit"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "net.debasishg" %% "redisclient" % "2.10"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    requireJsShim += "shim.js",
    requireJs += "main.js"
  )

}
