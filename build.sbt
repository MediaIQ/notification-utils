name := "notification-utils"

organization in ThisBuild := "com.miq.caps"

version in ThisBuild := "0.1"

scalaVersion in ThisBuild := "2.12.4"

lazy val build = (project in file("."))
  .aggregate(vertxUtils, parserUtils, commons, core)

lazy val vertxUtils = (project in file("vertx-utils"))
  .settings(
    name := "vertx-utils",
    commonSettings,
    libraryDependencies ++= dependencies.test ++ dependencies.circe ++ dependencies.vertx
  )

lazy val parserUtils = (project in file("parser-utils"))
  .settings(
    name := "parser-utils",
    commonSettings,
    libraryDependencies ++= dependencies.test ++ dependencies.circe ++ dependencies.vertx
  )
  .dependsOn(vertxUtils)

lazy val commons = (project in file("notification-utils-commons"))
  .settings(
    name := "notification-utils-commons",
    commonSettings,
    libraryDependencies ++= dependencies.test ++ dependencies.circe :+ dependencies.st4
  )

lazy val core = (project in file("notification-utils-core"))
  .settings(
    name := "notification-utils-core",
    commonSettings,
    libraryDependencies ++= dependencies.test ++ dependencies.vertx ++ dependencies.logging :+ dependencies.config :+ dependencies.commonsEmail,
    excludeDependencies ++= Seq(
      ExclusionRule("org.slf4j", "slf4j-log4j12"),
      ExclusionRule("log4j", "log4j")
    )
  )
  .dependsOn(commons % "test->test;compile->compile", vertxUtils % "test->test;compile->compile", parserUtils % "test->test;compile->compile")

lazy val sample = (project in file("sample-service"))
  .settings(
    name := "sample-service",
    libraryDependencies ++= Seq(
      dependencies.vertxWeb,
      "com.miq.caps" %% "notification-utils-core" % "0.1"
    )
  )

lazy val dependencies = new {

  private val configVersion = "1.3.2"
  val config = "com.typesafe" % "config" % configVersion

  private val scalatestVersion = "3.0.0"
  private val scalacheckVersion = "1.14.0"
  val test = Seq(
    "org.scalatest" %% "scalatest" % scalatestVersion % Test,
    "org.scalacheck" %% "scalacheck" % scalacheckVersion % Test
  )

  private val vertxVersion = "3.5.0"
  val vertx = Seq(
    "io.vertx" %% "vertx-lang-scala" % vertxVersion,
    "io.vertx" %% "vertx-web-client-scala" % vertxVersion
  )
  val vertxWeb = "io.vertx" %% "vertx-web-scala" % vertxVersion

  private val circeVersion = "0.9.3"
  val circe = Seq(
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-generic-extras" % circeVersion
  )

  private val scalaLoggingVersion = "3.8.0"
  private val logbackVersion = "1.2.3"
  val logging = Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion
  )

  private val commonsEmailVersion = "1.5"
  val commonsEmail = "org.apache.commons" % "commons-email" % commonsEmailVersion

  private val st4Version = "4.0.8"
  val st4 = "org.antlr" % "ST4" % st4Version

}

lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-encoding",
  "utf8"
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  fork in run := true,

  publishArtifact in Test := true,

  // ensures minimum coverage for package to be built
  coverageMinimum := 80,
  coverageFailOnMinimum := true
)
