import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KTable
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.slf4j.{LoggerFactory}

import java.util.{Collections, Properties}

object NetworkWordsCountApp extends App {

  import org.apache.kafka.streams.scala.ImplicitConversions._
  import org.apache.kafka.streams.scala.serialization.Serdes._

  private[this] val InputTopic = "socket-content"
  private[this] val OutputTopic = "network-words-count"
  private[this] val SchemaRegistryUrl = "http://schema-registry:8081" // to run locally "http://localhost:8081"
  private[this] val BootstrapServer = "broker:29092" // to run locally replace "localhost:9092"
  private[this] val logger = LoggerFactory.getLogger("NetworkWordsCountApp")


  private[this] val props: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "words-count-application")
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BootstrapServer)
    p.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SchemaRegistryUrl)
    p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    p
  }
  logger.info(s"Start NetworkWordsCount app with following properties ${props}")

  private[this] implicit val socketContentValueSerde: Serde[SocketContentValue] = {
    val s = new SpecificAvroSerde[SocketContentValue]
    s.configure(Collections.singletonMap(
      AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SchemaRegistryUrl),
      false)
    s
  }

  private[this] implicit val wordCountValueSerde: Serde[WordCountValue] = {
    val s = new SpecificAvroSerde[WordCountValue]
    s.configure(Collections.singletonMap(
      AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SchemaRegistryUrl),
      false)
    s
  }

  private val builder = new StreamsBuilder
  // 1 - stream from Kafka (example message : <null , message: "Word1 word1 word2">)
  private val socketContent = builder.stream[String, SocketContentValue](InputTopic)

  private val wordsCount: KTable[String, WordCountValue] = socketContent
    // 2 - map to lowercase ( => <null , "word1 word1 word2"> )
    .mapValues(v => v.message.toLowerCase())
    // 3- split value by space (=> <null , "word1">,<null ,"word1>, <null,word2>)
    .flatMapValues(v => v.split(" "))
    // 4 - select key to apply key => <"word1","word1">, <"word1","word1">,<"word2","word2">
    .selectKey((_, word) => word)
    // 5 - group by key => (<"word1","word1">, <"word1","word1">), (<"word2","word2">)
    .groupByKey
    //6 - count occurrences in each group <"word1", 2>, < "word2",1>
    .count()
    .mapValues((k, v) => new WordCountValue(k, v.toInt))

  wordsCount.toStream.to(OutputTopic)

  private val streams: KafkaStreams = new KafkaStreams(builder.build(), props)
  streams.start()
//  logger.info("Stream started...")

  sys.ShutdownHookThread {
    streams.close()
//    logger.info("Stream closed...")
  }
}
