package com.yusj.starfish.checkpoints;

import com.yusj.starfish.checkpoints.source.EventSourceWithChk;
import com.yusj.starfish.checkpoints.window.WindowStatisticWithChk;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

public class CheckpointMain {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 设置相关配置，并开启checkpoint功能
//        env.setStateBackend(new FsStateBackend("hdfs://flink/flink-checkpoint/checkpoint/"));
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setCheckpointInterval(6000);

        // 应用逻辑
        env.addSource(new EventSourceWithChk())
                .keyBy(0)
                .window(TumblingProcessingTimeWindows.of(Time.seconds(60)))
                .apply(new WindowStatisticWithChk())
                .print();

        env.execute();
    }
}
