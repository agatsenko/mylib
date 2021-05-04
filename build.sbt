lazy val mylibTester = (project in file("./modules/mylib-tester")).
    settings(build.scalaCommonSettings: _*).
    settings(
      name := "mylib-tester",
      libraryDependencies ++= Seq(
        build.depends.logbackClassic,
        build.depends.h2,
        build.depends.h2Mvstore,
        build.depends.hikaricp,
        build.depends.slick,
        build.depends.slickHikaricp,
        build.depends.scalikejdbc,
        build.depends.doobieCore,
        build.depends.doobieH2,
        build.depends.doobieHikari,
        build.depends.mangoSql,
        build.depends.mangoCommon,
        build.depends.mangoServices,
//        "org.mongodb" % "bson" % "3.12.1",
        "org.mongodb.scala" %% "mongo-scala-bson" % "2.8.0",
      )
    )

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

lazy val mylibDocStorage = (project in file("./modules/mylib-doc-storage"))
    .settings(build.scalaCommonSettings: _*)
    .settings(
      name := "mylib-doc-storage",
      libraryDependencies ++= Seq(
        build.depends.mangoCommon,
      ),
    )
    .dependsOn(
    )

lazy val mylibFsDocStorage = (project in file("./modules/mylib-fs-doc-storage"))
    .settings(build.scalaCommonSettings: _*)
    .settings(
      name := "mylib-fs-doc-storage",
      libraryDependencies ++= Seq(
        build.depends.mangoCommon,
      ),
    )
    .dependsOn(
      mylibDocStorage,
    )

lazy val mylibCore = (project in file("./modules/mylib-core"))
    .settings(build.scalaCommonSettings: _*)
    .settings(
      name := "mylib-core",
      libraryDependencies ++= Seq(
        build.depends.mangoCommon,
      ),
    )
    .dependsOn(
      mylibDocStorage,
    )

lazy val mylibHttpApi = (project in file("./modules/mylib-http-api"))
    .settings(build.scalaCommonSettings: _*)
    .settings(
      name := "mylib-http-api",
      libraryDependencies ++= Seq(
        build.depends.akkaStream,
        build.depends.akkaHttp,
      )
    )
    .dependsOn(
      mylibCore,
    )

lazy val mylibFrontendDesktop = (project in file("./modules/mylib-frontend-desktop"))
    .enablePlugins(
      NpmPlugin,
    )
    .settings(build.commonSettings: _*)
    .settings(
      name := "mylib-frontend-desktop",

      clean := Def.sequential(
        NpmKeys.npmRun.toTask(" clean"),
        clean,
      ).value,

      compile in Compile := Def.task {
        NpmKeys.npmRun.toTask(" lint").value
        (compile in Compile).value
      }.value,

      Keys.`package` in Compile := Def.task {
        NpmKeys.npmRun.toTask(" electron:build").value
        (Keys.`package` in Compile).value
      }.value,
    )
    .dependsOn(
    )


////////////////////////////////////////////

//#region test modules

lazy val mylibTest = (project in file("./modules/mylib-test")).
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

lazy val mylibCoreInfrastructure = (project in file("./modules/mylib-core-infrastructure")).
    settings(build.scalaCommonSettings: _*).
    settings(
      name := "mylib-core-infrastructure",
      libraryDependencies ++= Seq(
        build.depends.mangoCommon,
        build.depends.akkaStream,
      ),
    ).
    dependsOn(
      mylibTest % Test,
    )

lazy val mylibCoreDomain = (project in file("./modules/mylib-core-domain")).
    settings(build.scalaCommonSettings: _*).
    settings(
      name := "mylib-core-domain",
      libraryDependencies ++= Seq(
        build.depends.monix,
        build.depends.mangoCommon,
        build.depends.mangoServices,

        build.depends.mangoServicesMacwire % Test,
      ),
    ).
    dependsOn(
      mylibCoreInfrastructure,

      mylibTest % Test,
    )

lazy val mylibCorePersist = (project in file("./modules/mylib-core-persist")).
    settings(build.scalaCommonSettings: _*).
    settings(
      name := "mylib-core-persist",
      libraryDependencies ++= Seq(
        build.depends.mangoCommon,
      ),
    ).
    dependsOn(
      mylibCoreInfrastructure,
    )

lazy val mylibCoreJdbc = (project in file("./modules/mylib-core-jdbc")).
    settings(build.scalaCommonSettings: _*).
    settings(
      name := "mylib-core-jdbc",
      libraryDependencies ++= Seq(
        build.depends.mangoCommon,
        build.depends.scalikejdbc,
      ),
    ).
    dependsOn(
      mylibCoreInfrastructure,
      mylibCorePersist,
    )

lazy val mylibCoreH2 = (project in file("./modules/mylib-core-h2")).
    settings(build.scalaCommonSettings: _*).
    settings(
      name := "mylib-core-h2",
      libraryDependencies ++= Seq(
        build.depends.mangoCommon,
        build.depends.scalikejdbc,
        build.depends.h2,
      ),
    ).
    dependsOn(
      mylibCoreInfrastructure,
      mylibCorePersist,

      mylibTest % Test,
    )

//#endregion core modules

//#region root module

lazy val mylib = (project in file(".")).
    settings(build.commonSettings: _*).
    settings(
      name := "mylib-parent",
    ).
    aggregate(
      mylibDocStorage,
      mylibFsDocStorage,
      mylibCore,
      mylibHttpApi,
      mylibFrontendDesktop,

      mylibTest,

      mylibCoreInfrastructure,
      mylibCoreDomain,
      mylibCorePersist,
      mylibCoreJdbc,
      mylibCoreH2,
    )

//#endregion root module
