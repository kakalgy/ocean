package com.yusj.starfish.entry;

import com.yusj.starfish.map.CountWithKeyesState;
import com.yusj.starfish.source.SocketSource;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

/**
 * @Description
 * @Author kakalgy
 * @Date 2018/10/29 22:47
 **/
public class KeyedStateMain {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        env.enableCheckpointing(60000);
        CheckpointConfig checkpointConfig = env.getCheckpointConfig();
        checkpointConfig.setMinPauseBetweenCheckpoints(30000L);
        checkpointConfig.setCheckpointTimeout(10000L);
        checkpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        //使用Socket作为数据源
        SocketSource socketSource = new SocketSource(args);
        env.socketTextStream(socketSource.getHostName(), socketSource.getPort(), "\n").map(new MapFunction<String, Tuple2<Long, Long>>() {
            public Tuple2<Long, Long> map(String s) throws Exception {
                String[] str = s.split(",");
                return new Tuple2<Long, Long>(Long.parseLong(str[0]), Long.parseLong(str[1]));
            }
        }).flatMap(new CountWithKeyesState()).addSink(new SinkFunction<Tuple2<Long, Long>>() {
            public void invoke(Tuple2<Long, Long> value, Context context) throws Exception {

            }
        });

        env.execute("OperatorStateMain");
    }
}
