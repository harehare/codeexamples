lazy val scalaJSReactVersion = "1.6.0"

ThisBuild / scalaVersion := "2.13.1"
ThisBuild / version := "0.1.0"
ThisBuild / organization := "com.codeexamples"
ThisBuild / organizationName := "harehare"

ThisBuild / developers := List(
  Developer(
    id = "harehare",
    name = "Takahiro Sato",
    email = "harehare1110@gmail.com",
    url = url("https://harehare.github.io/flutter-portfolio")
  )
)

ThisBuild / scalafixDependencies += "com.nequissimus" %% "sort-imports" % "0.5.0"

lazy val root = project
  .in(file("."))
  .aggregate(client)

lazy val client = project
  .in(file("client"))
  .settings(
    name := "CodeExamples",
    scalaJSUseMainModuleInitializer := true,
    resolvers += "jitpack" at "https://jitpack.io",
    scalacOptions += "-P:scalajs:sjsDefinedByDefault",
    libraryDependencies ++= Seq(
      "org.scala-js"                      %%% "scalajs-dom"        % "1.0.0",
      "com.github.japgolly.scalajs-react" %%% "core"               % scalaJSReactVersion,
      "com.github.japgolly.scalajs-react" %%% "extra"              % scalaJSReactVersion,
      "org.typelevel"                     %%% "mouse"              % "0.24",
      "io.suzaku"                         %%% "diode"              % "1.1.7",
      "io.suzaku"                         %%% "diode-react"        % "1.1.7.160",
      "com.softwaremill.macwire"          %% "macros"              % "2.3.3",
      "com.github.fdietze"                % "scala-js-fontawesome" % "-SNAPSHOT"
    ),
    npmDependencies in Compile ++= Seq(
      "react"     -> "16.12.0",
      "react-dom" -> "16.12.0"
    ),
    npmDevDependencies in Compile ++= Seq(
      "webpack-merge"                      -> "4.2.2",
      "file-loader"                        -> "5.1.0",
      "image-webpack-loader"               -> "6.0.0",
      "css-loader"                         -> "3.4.2",
      "style-loader"                       -> "1.1.3",
      "url-loader"                         -> "3.0.0",
      "ts-loader"                          -> "6.2.1",
      "typescript"                         -> "3.8.2",
      "marked"                             -> "0.8.0",
      "monaco-editor"                      -> "0.20.0",
      "highlight.js"                       -> "10.0.2",
      "html-webpack-plugin"                -> "3.2.0",
      "preload-webpack-plugin"             -> "3.0.0-beta.4",
      "sass-loader"                        -> "7.3.1",
      "mini-css-extract-plugin"            -> "0.9.0",
      "node-sass"                          -> "4.13.1",
      "optimize-css-assets-webpack-plugin" -> "5.0.3",
      "svg-inline-loader"                  -> "0.8.2",
      "workbox-sw"                         -> "^5.1.3",
      "workbox-webpack-plugin"             -> "^5.1.3",
      "clean-webpack-plugin"               -> "^3.0.0",
      "optimize-css-assets-webpack-plugin" -> "^5.0.3"
    ),
    webpackConfigFile in fastOptJS := Some(baseDirectory.value / "dev.webpack.config.js"),
    webpackConfigFile in fullOptJS := Some(baseDirectory.value / "prod.webpack.config.js"),
    webpackConfigFile in Test := Some(baseDirectory.value / "common.webpack.config.js")
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
