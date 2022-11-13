scalaVersion := "2.12.13"
name := "kafka-streams-playground"

val kafkaApiVer = "3.0.0"

resolvers +=
  "Confluent" at "https://packages.confluent.io/maven/"

libraryDependencies ++= Seq(
  "org.apache.avro" % "avro" % "1.11.0",
  "org.apache.kafka" %% "kafka-streams-scala" % kafkaApiVer,
  "org.apache.kafka" % "kafka-clients" % kafkaApiVer,
  "io.confluent" % "kafka-streams-avro-serde" % "7.0.0",
  "org.slf4j" %  "slf4j-api" % "1.7.25",
  "org.slf4j" %  "slf4j-log4j12" % "1.7.25"
)

Compile / avroSource := (sourceDirectory.value / "main/resources/avro-schemas")
Compile / avroGenerate / target := (baseDirectory.value.getAbsoluteFile / "target/generated-sources")
Compile / avroStringType := "String"


