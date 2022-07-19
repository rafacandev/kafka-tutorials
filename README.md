kafka-tutorials
---------------
This repository contains a series of kafka tutorials.
These tutorials are meant to be a step-by-step guide for developing applications with Java, Spring Boot and Kafka. 

Kafka Vocabulary
----------------
As summurized at [Using Kafka with Spring Boot](https://reflectoring.io/spring-boot-kafka/) by _Nandan BN_:

**Producer**: A producer is a client that sends messages to the Kafka server to the specified topic.
**Consumer**: Consumers are the recipients who receive messages from the Kafka server.
**Broker**: Brokers can create a Kafka cluster by sharing information using Zookeeper. A broker receives messages from producers and consumers fetch messages from the broker by topic, partition, and offset.
**Cluster**: Kafka is a distributed system. A Kafka cluster contains multiple brokers sharing the workload.
**Topic**: A topic is a category name to which messages are published and from which consumers can receive messages.
**Partition**: Messages published to a topic are spread across a Kafka cluster into several partitions. Each partition can be associated with a broker to allow consumers to read from a topic in parallel.
**Offset**: Offset is a pointer to the last message that Kafka has already sent to a consumer.


### Tutorial 1: Run a kafka cluster with docker
In this tutorial we are going to run a kafka cluster from a docker compose and explore some basic kafka commands.

### Tutorial 2: A simple spring boot with KafkaTemplate
In this tutorial we are going to run a Spring Boot application producing messages with KafkaTemplate and consuming messages with KafkaMessageListenerContainer.



