package com.yusj.starfish.stream.map;

import com.yusj.starfish.stream.pojo.FishState;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.util.Collector;

public class FishStateTupleFlatMap extends RichFlatMapFunction<Tuple3<String, Long, Long>, Tuple3<String, Long, FishState>> {

    @Override
    public void flatMap(Tuple3<String, Long, Long> value, Collector<Tuple3<String, Long, FishState>> out) throws Exception {
        FishState fishState = new FishState(value.f0, value.f1, value.f2);
        out.collect(new Tuple3<>(value.f0, value.f1, fishState));
    }
}
