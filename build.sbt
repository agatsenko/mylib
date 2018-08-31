////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// common modules

lazy val `mylib-infrastructure` = (project in file("./modules/common/mylib-infrastructure")).
    settings(build.scalaCommonSettings: _*).
    settings(
      name := "mylib-infrastructure",
      libraryDependencies ++= Seq(
      ),
    )


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// storage modules

lazy val `mylib-docstore-embedded` = (project in file("./modules/storage/mylib-docstore-embedded")).
    settings(build.scalaCommonSettings: _*).
    settings(
      name := "mylib-docstore-embedded",
      libraryDependencies ++= Seq(
        build.depends.circeCore,
        build.depends.circeParser,
        build.depends.circeGeneric,
        build.depends.circeOptics,
      ),
    )

lazy val `mylib-docstore-embedded-h2` = (project in file("./modules/storage/mylib-docstore-embedded-h2")).
    settings(build.scalaCommonSettings: _*).
    settings(
      name := "mylib-docstore-embedded-h2",
      libraryDependencies ++= Seq(
        build.depends.h2,
        build.depends.hikariCp % Test
      ),
    ).
    dependsOn(
      `mylib-docstore-embedded`,
    )


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// core modules

lazy val `mylib-core-infrastructure` = (project in file("./modules/core/mylib-core-infrastructure")).
    settings(build.scalaCommonSettings: _*).
    settings(
      name := "mylib-core-infrastructure",
      libraryDependencies ++= Seq(
      ),
    )

lazy val `mylib-web-play` = (project in file("./modules/web/mylib-web-play")).
    enablePlugins(
      PlayScala,
    ).
    settings(build.scalaCommonSettings: _*).
    settings(
      name := "mylib-web-play",
      libraryDependencies ++= Seq(
        guice,
        build.depends.scalatestplusPlay,
      ),
      // Adds additional packages into Twirl
      //TwirlKeys.templateImports += "com.agat.controllers._"

      // Adds additional packages into conf/routes
      // play.sbt.routes.RoutesKeys.routesImport += "com.agat.binders._"
    ).
    dependsOn(
      `mylib-core-infrastructure`,
    )


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// root module

lazy val mylib = (project in file(".")).
    settings(build.commonSettings: _*).
    settings(
      name := "mylib",
    ).
    aggregate(
      `mylib-infrastructure`,

      `mylib-docstore-embedded`,
      `mylib-docstore-embedded-h2`,

      `mylib-core-infrastructure`,

      `mylib-web-play`,
    )
