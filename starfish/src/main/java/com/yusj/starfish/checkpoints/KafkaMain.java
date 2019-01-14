package com.yusj.starfish.checkpoints;

import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.functions.RichReduceFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.serialization.TypeInformationSerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Properties;

public class KafkaMain {

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "datanode1:9092,datanode2:9092");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "kafka-chk-group");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.enableCheckpointing(60000);
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
//        env.getCheckpointConfig().setCheckpointInterval(6000);

        FlinkKafkaConsumer011<String> kafkaConsumer1 = new FlinkKafkaConsumer011<String>("kafka-chk",
                new SimpleStringSchema(), properties);

        DataStream<String> d1 = env.addSource(kafkaConsumer1);

        d1.map(new RichMapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String value) throws Exception {
                System.out.println("Map: " + value);
                return new Tuple2<>(value, 1);
            }
        }).keyBy(0)
                .window(TumblingProcessingTimeWindows.of(Time.seconds(60))).reduce(new ReduceFunction<Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> reduce(Tuple2<String, Integer> value1, Tuple2<String, Integer> value2) throws Exception {
                System.out.println("Reduce: " + value1.f0 + "times: " + (value1.f1 + value2.f1));
                return new Tuple2<>(value1.f0, value1.f1 + value2.f1);
            }
        }).print();

        env.execute("kafka-checkpoint");
    }

}
