organization := "com.github.alari"

name := "wscalacl"

version := "0.1-SNAPSHOT"

crossScalaVersions := Seq("2.10.4", "2.11.3")

sbtVersion := "0.13.5"

libraryDependencies ++= {
  Seq(
    "org.specs2" %% "specs2" % "2.3.12" % "test"
  )
}

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Ywarn-dead-code",
  "-language:_",
  "-target:jvm-1.7",
  "-encoding", "UTF-8"
)

publishTo := Some(Resolver.file("file",  new File( "/mvn-repo" )) )

testOptions in Test += Tests.Argument("junitxml")