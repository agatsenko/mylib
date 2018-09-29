import sbt._
import Keys._

object build {
  object project {
    val org = "io.agatsenko"
    val ver = "0.1.0"
  }

  object ver {
    val java = "1.8"
//    val crossScala = Seq("2.11.12", "2.12.6")
    val crossScala = Seq("2.12.6")

    val scalaAsync = "0.9.7"
    val enumeratum = "1.5.+"

    val slf4j = "1.7.+"
    val logback = "1.2.+"
    val scalaLogging = "3.9.+"
    val janino = "3.0.+"

    val circe = "0.9.+"
    val macwire = "2.3.+"
    val mango = "0.+"

    val mongoScala = "2.4.+"
    val scalikejdbc = "3.3.+"
    val hikariCp = "2.7.+"
    val h2 = "1.4.+"

    val scalatest = "3.0.5"
    val scalatestplusPlay = "3.1.+"
  }

  object depends {
    val scalaLib = "org.scala-lang" % "scala-library"

    val scalaAsync = "org.scala-lang.modules" %% "scala-async" % ver.scalaAsync
    val enumeratum = "com.beachape" %% "enumeratum" % ver.enumeratum

    val slf4jApi = "org.slf4j" % "slf4j-api" % ver.slf4j
    val logbackClassic = "ch.qos.logback" % "logback-classic" % ver.logback
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % ver.scalaLogging
    val janio = "org.codehaus.janino" % "janino" % ver.janino

    val circeCore = "io.circe" %% "circe-core" % ver.circe
    val circeGeneric = "io.circe" %% "circe-generic" % ver.circe
    val circeParser = "io.circe" %% "circe-parser" % ver.circe
    val circeOptics = "io.circe" %% "circe-optics" % ver.circe

    val macwireMacros = "com.softwaremill.macwire" %% "macros" % ver.macwire
    val macwireUtil = "com.softwaremill.macwire" %% "util" % ver.macwire

    val mangoCommon = "io.mango" %% "mango-common" % ver.mango
    val mangoServices = "io.mango" %% "mango-services" % ver.mango
    val mangoServicesMacwire = "io.mango" %% "mango-services-macwire" % ver.mango

    val mongoScalaDriver = "org.mongodb.scala" %% "mongo-scala-driver" % ver.mongoScala
    val scalikejdbc = "org.scalikejdbc" %% "scalikejdbc" % ver.scalikejdbc
    val hikariCp = "com.zaxxer" % "HikariCP" % ver.hikariCp
    val h2 = "com.h2database" % "h2" % ver.h2

    val scalatest = "org.scalatest" %% "scalatest" % ver.scalatest
    val scalatestplusPlay = "org.scalatestplus.play" %% "scalatestplus-play" % ver.scalatestplusPlay
  }

  val commonSettings = Seq(
    organization := project.org,
    version := project.ver,

    crossScalaVersions := ver.crossScala,

    // some info about scala compile options see in
    // http://blog.threatstack.com/useful-scalac-options-for-better-scala-development-part-1
    scalacOptions ++= Seq(
      // JVM target
      s"-target:jvm-${ver.java}",
      // Emit warning and location for usages of deprecated APIs.
      "-deprecation",
      // Enable detailed unchecked (erasure) warnings
      // Non variable type-arguments in type patterns are unchecked since they are eliminated by erasure
      "-unchecked",
      // Emit warning and location for usages of features that should be imported explicitly.
      "-feature",
      // Enable experimental extensions.
      "-Xexperimental",
      // Wrap field accessors to throw an exception on uninitialized access.
      "-Xcheckinit",
      // Enable recommended additional warnings.
      "-Xlint:_",
      // Fail the compilation if there are any warnings.
      //, "-Xfatal-warnings"
      // Warn when local and private vals, vars, defs, and types are unused.
      "-Ywarn-unused",
      // Warn when numerics are widened.
      //"-Ywarn-numeric-widen",
    ),
    compileOrder := CompileOrder.Mixed,

    excludeFilter in unmanagedSources := HiddenFileFilter || ".keepdir",
    excludeFilter in unmanagedResources := HiddenFileFilter || ".keepdir",

    publishM2Configuration := publishM2Configuration.value.withOverwrite(true),
  )

  val scalaCommonSettings = commonSettings ++ Seq(
    libraryDependencies ++= Seq(
      depends.scalaLib % scalaVersion.value,
      depends.slf4jApi,
      depends.scalaLogging,

      depends.logbackClassic % Test,
      depends.scalatest % Test,
    ),
  )
}
