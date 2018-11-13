package com.yusj.starfish.stream.multiple;

import com.yusj.starfish.source.SocketSource;
import com.yusj.starfish.stream.map.FishStateFlatMap;
import com.yusj.starfish.stream.map.FishStateTupleFlatMap;
import com.yusj.starfish.stream.pojo.FishState;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.FoldFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.serialization.TypeInformationSerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import javax.xml.crypto.Data;
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
                .map(new MapFunction<String, Tuple3<String, Long, Long>>() {
                    @Override
                    public Tuple3<String, Long, Long> map(String value) throws Exception {
                        String str[] = value.split("\\s");
                        return new Tuple3<String, Long, Long>(str[0], Long.parseLong(str[1]), Long.parseLong(str[2]));
                    }
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
        DataStream<Tuple3<String, Long, Long>> filtered = source.filter(value -> !value.f0.equals("test"));

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

        //select 分为5条Stream
        DataStream<Tuple3<String, Long, Long>> first = splitStream.select("first");
        DataStream<Tuple3<String, Long, Long>> second = splitStream.select("second");
        DataStream<Tuple3<String, Long, Long>> third = splitStream.select("third");
        DataStream<Tuple3<String, Long, Long>> forth = splitStream.select("third");
        DataStream<Tuple3<String, Long, Long>> dozens = splitStream.select("second", "third");


        ////////////////////////////////////////////////
        ////
        ////    First
        ////
        ////////////////////////////////////////////////
        //project 将first流的结构由Tuple3<String, Long, Long>改为Tuple2<Long, String>
        DataStream<Tuple2<Long, String>> firstProjectStream = first.project(2, 0);


        ////////////////////////////////////////////////
        ////
        ////    Second
        ////
        ////////////////////////////////////////////////
        //flatMap 将second流转换为FishState对象
        DataStream<FishState> secondFlatMapStream = second.flatMap(new FishStateFlatMap());
        //keyby 将third流按照String进行keyby
        KeyedStream<FishState, Tuple> secondKeyStream = secondFlatMapStream.keyBy("fishName");
        DataStream<FishState> aggregationSecondStream1 = secondKeyStream.max("count");
        aggregationSecondStream1.addSink(new SinkFunction<FishState>() {
            @Override
            public void invoke(FishState value, Context context) throws Exception {
                System.out.println("aggregationSecondStream1: " + value.getFishName() + ", " + value.getCount() + ", " + value.getTimestamp());
            }
        });

        ////////////////////////////////////////////////
        ////
        ////    Third
        ////
        ////////////////////////////////////////////////
        //keyby 将third流按照String进行keyby
        KeyedStream<Tuple3<String, Long, Long>, Tuple> thirdKeyStream = third.keyBy(0);

        //Fold admin 1 2 || admin 2 3 || admin 3 4 => (testadmin,1001,2002) || (testadminadmin,1003,2005) || (testadminadminadmin,1006,2009)
        DataStream<Tuple3<String, Long, Long>> foldStream = thirdKeyStream.fold(new Tuple3<>("test", 1000L, 2000L), new FoldFunction<Tuple3<String, Long, Long>, Tuple3<String, Long, Long>>() {
            @Override
            public Tuple3<String, Long, Long> fold(Tuple3<String, Long, Long> accumulator, Tuple3<String, Long, Long> value) throws Exception {
                return new Tuple3<>(accumulator.f0 + value.f0, accumulator.f1 + value.f1, accumulator.f2 + value.f2);
            }
        });

        DataStream<Tuple3<String, Long, Long>> aggregationStream2 = thirdKeyStream.max(2);
        DataStream<Tuple3<String, Long, Long>> aggregationStream4 = thirdKeyStream.maxBy(2);

        aggregationStream2.addSink(new SinkFunction<Tuple3<String, Long, Long>>() {
            @Override
            public void invoke(Tuple3<String, Long, Long> value, Context context) throws Exception {
                System.out.println("aggregationStream2: " + value.f0 + ", " + value.f1 + ", " + value.f2);
            }
        });
        aggregationStream4.addSink(new SinkFunction<Tuple3<String, Long, Long>>() {
            @Override
            public void invoke(Tuple3<String, Long, Long> value, Context context) throws Exception {
                System.out.println("aggregationStream4: " + value.f0 + ", " + value.f1 + ", " + value.f2);
            }
        });

        ////////////////////////////////////////////////
        ////
        ////    Forth
        ////
        ////////////////////////////////////////////////

        DataStream<Tuple3<String, Long, FishState>> forthFlatMapStream = forth.flatMap(new FishStateTupleFlatMap());
        //keyby 将third流按照String进行keyby
        KeyedStream<Tuple3<String, Long, FishState>, Tuple> forthKeyStream = forthFlatMapStream.keyBy(0);
        forthKeyStream.window(TumblingProcessingTimeWindows.of(Time.seconds(20))).reduce(new ReduceFunction<Tuple3<String, Long, FishState>>() {
            @Override
            public Tuple3<String, Long, FishState> reduce(Tuple3<String, Long, FishState> value1, Tuple3<String, Long, FishState> value2) throws Exception {

                return new Tuple3<>(value1.f0, value1.f1 + value1.f1, value2.f2);
            }
        }).windowAll(TumblingProcessingTimeWindows.of(Time.seconds(20))).sum(1).addSink(new SinkFunction<Tuple3<String, Long, FishState>>() {
            @Override
            public void invoke(Tuple3<String, Long, FishState> value, Context context) throws Exception {
                System.out.println("forthKeyStream: " + value.f0 + ", " + value.f1 + ", " + value.f2);
            }
        });

        ////////////////////////////////////////////////
        ////
        ////    Dozens
        ////
        ////////////////////////////////////////////////


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
