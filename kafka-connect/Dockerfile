FROM confluentinc/cp-kafka-connect-base:5.4.6
ADD connect-socket-source-1.0.jar /etc/kafka-connect/jars/connect-socket-source-1.0.jar
RUN  confluent-hub install --no-prompt confluentinc/kafka-connect-jdbc:latest
