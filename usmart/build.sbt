name := """usmart"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  javaCore,
  cache,
  javaWs,
  filters,
  evolutions,
  "org.webjars" %% "webjars-play" % "2.5.0",
  "org.webjars" % "bootstrap" % "3.3.6",
  "com.adrianhurt" %% "play-bootstrap" % "1.1-P25-B3-SNAPSHOT",
  "be.objectify" %% "deadbolt-java" % "2.5.0",
  "com.feth"      %% "play-authenticate" % "0.8.1-SNAPSHOT"
)

// add resolver for deadbolt and easymail snapshots
resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Apache" at "http://repol.maven.org/maven2/"
)

routesGenerator := InjectedRoutesGenerator