lazy val commonSettings = Seq(
  scalaVersion := "2.12.13"
)

val kafkaApiVer = "3.0.0"

lazy val rootProject = (project in file("."))
  .settings(commonSettings: _*)
  .settings(publishArtifact := false, name := "kafka-streams-playground")
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(commonSettings: _*)
  .settings(
    dockerBaseImage := "openjdk:8u212-jdk-stretch",
    packageName in Docker := "words-count-app",
    dockerUpdateLatest := true,
    logLevel := Level.Info
  ).settings(
  resolvers +=
    "Confluent" at "https://packages.confluent.io/maven/",
  libraryDependencies ++= Seq(
    "org.apache.avro" % "avro" % "1.11.0",
    "org.apache.kafka" %% "kafka-streams-scala" % kafkaApiVer,
    "org.apache.kafka" % "kafka-clients" % kafkaApiVer,
    "io.confluent" % "kafka-streams-avro-serde" % "7.0.0",
    "org.slf4j" %  "slf4j-api" % "1.7.25",
    "org.slf4j" %  "slf4j-log4j12" % "1.7.25"
  )).settings(
  avroSource in Compile := (sourceDirectory.value / "main/resources/avro-schemas"),
  avroGenerate / target in Compile := (baseDirectory.value.getAbsoluteFile / "target/generated-sources"),
  avroStringType in Compile := "String"
)

