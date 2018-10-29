package com.yusj.starfish.map;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

/**
 * @Description without implement checkpoint
 * <p>
 * FlatMapFunction 无状态函数
 * RichFlatMapFunction 有状态函数 可以获取getRunTimeContext().getState
 * @Author kakalgy
 * @Date 2018/10/29 22:36
 **/
public class CountWithKeyesState extends RichFlatMapFunction<Tuple2<Long, Long>, Tuple2<Long, Long>> {

    private transient ValueState<Tuple2<Long, Long>> sum;

    @Override
    public void flatMap(Tuple2<Long, Long> input, Collector<Tuple2<Long, Long>> output) throws Exception {
        //access value state
        Tuple2<Long, Long> currentSum = sum.value();

        //update the count
        currentSum.f0 += 1;

        //add the second field of the input value
        currentSum.f1 += input.f1;

        //update the state
        sum.update(currentSum);

        //if the count reaches 2, wait the avarage and clear the state
        if (currentSum.f0 >= 3) {
            output.collect(new Tuple2<Long, Long>(input.f0, currentSum.f1 / currentSum.f0));
            sum.clear();
        }
    }

    @Override
    public void open(Configuration config) throws Exception {
        ValueStateDescriptor<Tuple2<Long, Long>> descriptor = new ValueStateDescriptor<Tuple2<Long, Long>>("average",//the state name
                TypeInformation.of(new TypeHint<Tuple2<Long, Long>>() {
                }));

        sum = getRuntimeContext().getState(descriptor);
    }
}
