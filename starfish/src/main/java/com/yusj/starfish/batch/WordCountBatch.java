package com.yusj.starfish.batch;

import com.yusj.starfish.batch.map.Tokenizer;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;


public class WordCountBatch {

    public static void main(String[] args) {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        DataSet<String> text = env.readTextFile("/home/example/batch/set.text");

        DataSet<Tuple2<String, Integer>> counts = text.flatMap(new Tokenizer());

        counts.filter(new FilterFunction<Tuple2<String, Integer>>() {
            @Override
            public boolean filter(Tuple2<String, Integer> value) throws Exception {
                return !value.f0.equals("test");
            }
        }).groupBy(0).sum(1).writeAsCsv("/home/example/batch/set.csv", "\n", " ");


    }


}
