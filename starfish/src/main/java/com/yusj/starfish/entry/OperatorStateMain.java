package com.yusj.starfish.entry;

import com.yusj.starfish.map.CountWithOperatorStateFlatMap;
import com.yusj.starfish.source.SocketSource;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public class OperatorStateMain {
    public static void main(String[] args) throws Exception {


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        env.enableCheckpointing(60000);
        CheckpointConfig checkpointConfig = env.getCheckpointConfig();
        checkpointConfig.setMinPauseBetweenCheckpoints(30000L);
        checkpointConfig.setCheckpointTimeout(10000L);
        checkpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);


//        env.fromElements(1L, 2L, 3L, 4L, 1L).flatMap(new CountWithOperatorStateFlatMap()).addSink(new SinkFunction<String>() {
//            public void invoke(String value, Context context) throws Exception {
//
//            }
//        });

        //使用Socket作为数据源
        SocketSource socketSource = new SocketSource(args);
        env.socketTextStream(socketSource.getHostName(), socketSource.getPort(), "\n").map(new MapFunction<String, Long>() {
            public Long map(String s) throws Exception {
                return Long.parseLong(s);
            }
        }).flatMap(new CountWithOperatorStateFlatMap()).addSink(new SinkFunction<String>() {
            public void invoke(String value, Context context) throws Exception {
                System.out.println("sink result: " + value);
            }
        });

        env.execute("OperatorStateMain");
    }

}
