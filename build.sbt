//#region test modules

lazy val mylibTest = (project in file("./modules/test/mylib-test")).
    settings(build.scalaCommonSettings: _*).
    settings(
      name := "mylib-test",
      libraryDependencies ++= Seq(
        build.depends.slf4jApi,
        build.depends.logbackClassic,
        build.depends.janio,
        build.depends.mangoCommon,
      ),
    )

//#endregion test modules

//#region core modules

lazy val mylibCoreInfrastructure = (project in file("./modules/core/mylib-core-infrastructure")).
    settings(build.scalaCommonSettings: _*).
    settings(
      name := "mylib-core-infrastructure",
      libraryDependencies ++= Seq(
        build.depends.mangoCommon,
      ),
    ).
    dependsOn(
      mylibTest % Test,
    )

lazy val mylibCorePersistJdbc = (project in file("./modules/core/mylib-core-persist-jdbc")).
    settings(build.scalaCommonSettings: _*).
    settings(
      name := "mylib-core-persist-jdbc",
      libraryDependencies ++= Seq(
        build.depends.mangoCommon,
        build.depends.scalikejdbc,
      ),
    ).
    dependsOn(
      mylibCoreInfrastructure,
    )

lazy val mylibCorePersistH2 = (project in file("./modules/core/mylib-core-persist-h2")).
    settings(build.scalaCommonSettings: _*).
    settings(
      name := "mylib-core-persist-h2",
      libraryDependencies ++= Seq(
        build.depends.mangoCommon,
        build.depends.scalikejdbc,
        build.depends.h2,
      ),
    ).
    dependsOn(
      mylibCoreInfrastructure,

      mylibTest % Test,
    )

lazy val mylibCoreDomain = (project in file("./modules/core/mylib-core-domain")).
    settings(build.scalaCommonSettings: _*).
    settings(
      name := "mylib-core-domain",
      libraryDependencies ++= Seq(
        build.depends.mangoCommon,
        build.depends.mangoServices,

        build.depends.mangoServicesMacwire % Test,
      ),
    ).
    dependsOn(
      mylibCoreInfrastructure,

      mylibTest % Test,
    )

//#endregion core modules

//#region web modules

lazy val mylibWebPlay = (project in file("./modules/web/mylib-web-play")).
    enablePlugins(
      PlayScala,
    ).
    settings(build.scalaCommonSettings: _*).
    settings(
      name := "mylib-web-play",
      libraryDependencies ++= Seq(
        build.depends.mangoCommon,
        guice,
        build.depends.scalatestplusPlay % Test,
      ),
      // Adds additional packages into Twirl
      //TwirlKeys.templateImports += "com.agat.controllers._"

      // Adds additional packages into conf/routes
      // play.sbt.routes.RoutesKeys.routesImport += "com.agat.binders._"
    ).
    dependsOn(
      mylibCoreInfrastructure,

      mylibTest % Test,
    )

//#endregion web modules

//#region root module

lazy val mylib = (project in file(".")).
    settings(build.commonSettings: _*).
    settings(
      name := "mylib-parent",
    ).
    aggregate(
      mylibTest,

      mylibCoreInfrastructure,
      mylibCoreDomain,
      mylibCorePersistJdbc,
      mylibCorePersistH2,

      mylibWebPlay,
    )

//#endregion root module

