package com.yusj.seahorse.consumer;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Description
 * @Author kakalgy
 * @Date 2018/10/24 22:35
 **/
public class HighLevelConsumer {

    public static void main(String[] args) {
        args = new String[]{"hmaster:2181", "topic1", "group1", "consumer1"};

        Properties props = new Properties();
        props.put("zookeeper.connect", args[0]);
        props.put("zookeeper.session.timeout.ms", "3600000");
        props.put("group.id", args[2]);
        props.put("client.id", "testclient");
        props.put("consumer.id", args[3]);
        props.put("auto.offset.reset", "smallest");
        props.put("auto.offset.enable", "true");
        props.put("auto.offset.interval.ms", "6000");
//        props.put("offsets.storage", "kafka");
//        props.put("dual.commit","true");

        ConsumerConfig consumerConfig = new ConsumerConfig(props);
        ConsumerConnector consumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);

        Map<String, Integer> topicCountMap = new HashMap<>();
        topicCountMap.put(args[1], 1);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector.createMessageStreams(topicCountMap);

        KafkaStream<byte[], byte[]> stream1 = consumerMap.get(args[1]).get(0);
        ConsumerIterator<byte[], byte[]> iterator = stream1.iterator();
        while (iterator.hasNext()) {
            MessageAndMetadata<byte[], byte[]> messageAndMetadata = iterator.next();
            String message = String.format("Topic: %s, GroupId: %s, Consumer ID: %s, PartitionId: %s, Offset: %s, Message Key: %s, Message Payload: %s",
                    messageAndMetadata.topic(), args[2], args[3], messageAndMetadata.partition(), messageAndMetadata.offset(), new String(messageAndMetadata.key()),
                    new String(messageAndMetadata.message()));
            System.out.println(message);
//            consumerConnector.commitOffsets();
        }


    }
}
