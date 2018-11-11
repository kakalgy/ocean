package com.yusj.starfish.stream.multiple;

import com.yusj.starfish.source.SocketSource;
import org.apache.flink.api.common.serialization.TypeInformationSerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Properties;

public class MultipleInputMain {
    public static void main(String[] args) {

        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "datanode1:9092,datanode2:9092");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "multiple-group");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SocketSource socketSource = new SocketSource(args);
        DataStream<Long> socketData = env.socketTextStream(socketSource.getHostName(), socketSource.getPort(), "\n")
                .map(s -> Long.parseLong(s));

        FlinkKafkaConsumer011<Tuple1<Long>> kafkaConsumer1 = new FlinkKafkaConsumer011<>("multiple1",
                new TypeInformationSerializationSchema<>(new TypeHint<Tuple1<Long>>() {
                }.getTypeInfo(), env.getConfig()), properties);

        FlinkKafkaConsumer011<Tuple1<Long>> kafkaConsumer2 = new FlinkKafkaConsumer011<>("multiple2",
                new TypeInformationSerializationSchema<>(new TypeHint<Tuple1<Long>>() {
                }.getTypeInfo(), env.getConfig()), properties);

        DataStream<Long> kafkaData1 = env.addSource(kafkaConsumer1).map(s -> s.f0);
        DataStream<Long> kafkaData2 = env.addSource(kafkaConsumer2).map(s -> s.f0);

        DataStream<Long> source = socketData.union(kafkaData1, kafkaData2);

        source.print();

        try {
            env.execute("multiple-task");
        } catch (Exception e) {
            e.printStackTrace();
        }

//RocketMQ Source


    }
}
