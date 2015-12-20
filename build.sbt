name := """play24-guice-mybatis"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,


  // Other dependencies not mentioned in the article

  // For demonstration purposes we are using a more lightweight DB instead
   "mysql" % "mysql-connector-java" % "5.1.18",
  "com.typesafe.play" %% "play-mailer" % "3.0.1",
  "org.apache.commons" % "commons-email" % "1.3.1"
)

libraryDependencies += specs2 % Test

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

// Also copy the database file into the final generated zip by `activator dist`
// See http://stackoverflow.com/questions/12231862/how-to-make-play-framework-dist-command-adding-some-files-folders-to-the-final
mappings in Universal ++=
  (baseDirectory.value / "data.mv.db" get) map
    (x => x -> (x.getName))

playEbeanModels in Compile := Seq("model.*, security.model.*")

playEbeanDebugLevel := 4

playEbeanAgentArgs += ("detect" -> "false")

import java.io.File
import java.nio.file.Path

import com.github.play2war.plugin._
import com.sun.corba.se.spi.resolver.Resolver


Play2WarPlugin.play2WarSettings

Play2WarKeys.servletVersion := "3.0"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"


