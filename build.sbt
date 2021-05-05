val dottyVersion = "3.0.0-RC1"

lazy val macros =
  project
    .in(file("macros"))
    .settings(
      scalaVersion := dottyVersion
    )

lazy val root = project
  .in(file("core"))
  .settings(
    name := "dotty-simple",
    version := "0.1.0",
    scalaVersion := dottyVersion,
    libraryDependencies ++= Seq(
      "com.novocode" % "junit-interface" % "0.11" % "test"
    )
  )
  .dependsOn(macros)
