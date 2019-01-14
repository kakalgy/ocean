package com.yusj.starfish.batch.map;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {
    @Override
    public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
        String[] tokens = value.toLowerCase().split("\\W+");

        for (String token : tokens) {
            if (token.length() > 0) {
                Tuple2<String, Integer> result = new Tuple2<>(token, 1);
                System.out.println("Tokenizer: " + result);
                out.collect(result);
            }
        }
    }
}
