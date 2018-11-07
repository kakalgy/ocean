package com.yusj.starfish.map;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.util.Collector;

public class CountFlatMap extends RichFlatMapFunction<Long, String> {

    @Override
    public void flatMap(Long value, Collector<String> out) throws Exception {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(value);
        out.collect(stringBuffer.toString());
//                listElements.clear();
        System.out.println(stringBuffer.toString());

    }
}
