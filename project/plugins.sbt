addSbtPlugin("com.github.sbt" % "sbt-avro" % "3.4.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.19")
addSbtPlugin("com.softwaremill.sbt-softwaremill" % "sbt-softwaremill" % "1.6.1")


// Java sources compiled with one version of Avro might be incompatible with a
// different version of the Avro library. Therefore we specify the compiler
// version here explicitly.
libraryDependencies += "org.apache.avro" % "avro-compiler" % "1.11.0"
