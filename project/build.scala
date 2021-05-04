import sbt._
import Keys._

object build {
  object project {
    val org = "io.mylib"
    val ver = "0.1.0"
  }

  object ver {
    val java = "1.8"
    val scala = "2.13.1"

    val enumeratum = "1.5.+"

    val slf4j = "1.7.+"
    val logback = "1.2.+"
    val scalaLogging = "3.9.+"
    val janino = "3.0.+"

    val monix = "3.1.+"
    val circe = "0.12.+"
    val circeOptic = "0.12.+"
    val macwire = "2.3.+"
    val mango = "0.4.2"

    val akka = "2.6.+"
    val akkaHttp = "10.1.+"

    val h2 = "1.4.+"
    val hikaricp = "3.4.+"
    val slick = "3.3.+"
    val scalikejdbc = "3.4.+"
    val doobie = "0.8.+"

    val scalatest = "3.1.0"
  }

  object depends {
    val scalaLib = "org.scala-lang" % "scala-library"

    val enumeratum = "com.beachape" %% "enumeratum" % ver.enumeratum

    val slf4jApi = "org.slf4j" % "slf4j-api" % ver.slf4j
    val logbackClassic = "ch.qos.logback" % "logback-classic" % ver.logback
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % ver.scalaLogging
    // FIXME: need to review to remove
    val janio = "org.codehaus.janino" % "janino" % ver.janino

    val monix = "io.monix" %% "monix" % ver.monix

    val circeCore = "io.circe" %% "circe-core" % ver.circe
    val circeGeneric = "io.circe" %% "circe-generic" % ver.circe
    val circeParser = "io.circe" %% "circe-parser" % ver.circe
    val circeOptics = "io.circe" %% "circe-optics" % ver.circeOptic

    val macwireMacros = "com.softwaremill.macwire" %% "macros" % ver.macwire
    val macwireUtil = "com.softwaremill.macwire" %% "util" % ver.macwire

    val akkaActor = "com.typesafe.akka" %% "akka-actor" % ver.akka
    val akkaActorTyped = "com.typesafe.akka" %% "akka-actor-typed" % ver.akka
    val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % ver.akka
    val akkaStream = "com.typesafe.akka" %% "akka-stream" % ver.akka
    val akkaHttp = "com.typesafe.akka" %% "akka-http" % ver.akkaHttp

    val mangoCommon = "io.mango" %% "mango-common" % ver.mango
    val mangoServices = "io.mango" %% "mango-services" % ver.mango
    val mangoServicesMacwire = "io.mango" %% "mango-services-macwire" % ver.mango

    val h2 = "com.h2database" % "h2" % ver.h2
    val h2Mvstore = "com.h2database" % "h2-mvstore" % ver.h2
    val hikaricp = "com.zaxxer" % "HikariCP" % ver.hikaricp
    val slick = "com.typesafe.slick" %% "slick" % ver.slick
    val slickHikaricp = "com.typesafe.slick" %% "slick-hikaricp" % ver.slick
    val scalikejdbc = "org.scalikejdbc" %% "scalikejdbc" % ver.scalikejdbc
    val doobieCore = "org.tpolecat" %% "doobie-core" % ver.doobie
    val doobieH2 = "org.tpolecat" %% "doobie-h2" % ver.doobie
    val doobieHikari = "org.tpolecat" %% "doobie-hikari" % ver.doobie
    val mangoSql = "io.mango" %% "mango-sql" % ver.mango

    val scalatest = "org.scalatest" %% "scalatest" % ver.scalatest
  }

  val commonSettings = Seq(
    organization := project.org,
    version := project.ver,

    scalaVersion := ver.scala,

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

    resolvers += "github my artifacts" at "https://raw.githubusercontent.com/agatsenko/artifacts/master/maven",
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
