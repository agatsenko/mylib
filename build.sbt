lazy val `mylib-core-infrastructure` = (project in file("./modules/core/mylib-core-infrastructure")).
    settings(build.scalaCommonSettings: _*).
    settings(
      name := "mylib-core-infrastructure",
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

lazy val mylib = (project in file(".")).
    settings(build.commonSettings: _*).
    settings(
      name := "mylib",
    ).
    aggregate(
      `mylib-web-play`,
      `mylib-core-infrastructure`,
    )
