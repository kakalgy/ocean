package com.yusj.starfish.stream.multiple;

import com.yusj.starfish.source.SocketSource;
import com.yusj.starfish.stream.map.FishStateFlatMap;
import com.yusj.starfish.stream.pojo.FishState;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.serialization.TypeInformationSerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MultipleInputMain {
    public static void main(String[] args) {

        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "datanode1:9092,datanode2:9092");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "multiple-group");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SocketSource socketSource = new SocketSource(args);
        DataStream<Tuple3<String, Long, Long>> socketData = env.socketTextStream(socketSource.getHostName(), socketSource.getPort(), "\n")
                .map(value -> {
                    String str[] = value.split("\\s");
                    return new Tuple3<String, Long, Long>(str[0], Long.parseLong(str[1]), Long.parseLong(str[2]));
                }).name("socketSource");

        FlinkKafkaConsumer011<Tuple1<String>> kafkaConsumer1 = new FlinkKafkaConsumer011<>("multiple1",
                new TypeInformationSerializationSchema<>(new TypeHint<Tuple1<String>>() {
                }.getTypeInfo(), env.getConfig()), properties);

        FlinkKafkaConsumer011<String> kafkaConsumer2 = new FlinkKafkaConsumer011<>("multiple2",
                new TypeInformationSerializationSchema<>(new TypeHint<String>() {
                }.getTypeInfo(), env.getConfig()), properties);


        DataStream<Tuple1<String>> d1 = env.addSource(kafkaConsumer1);
        DataStream<Tuple3<String, Long, Long>> kafkaData1 = d1.map(new MapFunction<Tuple1<String>, Tuple3<String, Long, Long>>() {
            @Override
            public Tuple3<String, Long, Long> map(Tuple1<String> value) throws Exception {
                String str[] = value.f0.split("\\s");
                return new Tuple3<String, Long, Long>(str[0], Long.parseLong(str[1]), Long.parseLong(str[2]));
            }
        }).name("kafkaSource1");
//        DataStream<Long> kafkaData1 = env.addSource(kafkaConsumer1).map(s -> s.f0).name("kafkaSource1");

        DataStream<Tuple3<String, Long, Long>> kafkaData2 = env.addSource(kafkaConsumer2).map(new MapFunction<String, Tuple3<String, Long, Long>>() {
            @Override
            public Tuple3<String, Long, Long> map(String value) throws Exception {
                String str[] = value.split("\\s");
                return new Tuple3<String, Long, Long>(str[0], Long.parseLong(str[1]), Long.parseLong(str[2]));
            }
        }).name("kafkaSource2");
//        DataStream<Long> kafkaData2 = env.addSource(kafkaConsumer2).map(s -> s.f0).name("kafkaSource2");

        //Union Source
        DataStream<Tuple3<String, Long, Long>> source = socketData.union(kafkaData1, kafkaData2);

        //Filter 排除value.f0值为test的情况
        DataStream<Tuple3<String, Long, Long>> filtered = source.filter(value -> value.f0.equals("test"));

        //Split  将Stream按照value.f0的长度分为3份
        SplitStream<Tuple3<String, Long, Long>> splitStream = filtered.split(new OutputSelector<Tuple3<String, Long, Long>>() {
            @Override
            public Iterable<String> select(Tuple3<String, Long, Long> value) {
                List<String> output = new ArrayList<>();
                if (value.f0.length() % 3 == 0) {
                    output.add("first");
                } else if (value.f0.length() % 3 == 1) {
                    output.add("second");
                } else if (value.f0.length() % 3 == 2) {
                    output.add("third");
                }
                return output;
            }
        });

        //select 分为4条Stream
        DataStream<Tuple3<String, Long, Long>> first = splitStream.select("first");
        DataStream<Tuple3<String, Long, Long>> second = splitStream.select("second");
        DataStream<Tuple3<String, Long, Long>> third = splitStream.select("third");
        DataStream<Tuple3<String, Long, Long>> dozens = splitStream.select("second", "third");


        //project 将first流的结构由Tuple3<String, Long, Long>改为
        DataStream<Tuple2<Long, String>> firstProjectStream = first.project(2, 0);

        //flatMap 将second流转换为FishState对象
        DataStream<FishState> secondFlatMapStream = second.flatMap(new FishStateFlatMap());


//        source.print();

        try {
            env.execute("multiple-task");
        } catch (
                Exception e) {
            e.printStackTrace();
        }

//RocketMQ Source


    }
}
