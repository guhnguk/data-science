name := "spark210-snippet"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.1.0",
  "org.scalatest" %% "scalatest" % "2.2.2" % Test,
  "junit" % "junit" % "4.12" % Test
)