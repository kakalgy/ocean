package com.yusj.starfish.checkpoints.window;

import com.yusj.starfish.checkpoints.state.UDFState;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.checkpoint.ListCheckpointed;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

public class WindowStatisticWithChk implements WindowFunction<Tuple4<Long, String, String, Integer>, Long, Tuple, TimeWindow>, ListCheckpointed<UDFState> {
    private Long total = 0L;

    @Override
    public List<UDFState> snapshotState(long checkpointId, long timestamp) throws Exception {
        List<UDFState> listState = new ArrayList<>();
        UDFState udfState = new UDFState();
        udfState.setState(total);
        System.out.println("WindowStatisticWithChk.snapshotState: " + total);
        listState.add(udfState);
        return listState;
    }

    @Override
    public void restoreState(List<UDFState> state) throws Exception {
        UDFState udfState = state.get(0);
        total = udfState.getState();
        System.out.println("WindowStatisticWithChk.restore: " + total);
    }

    /**
     * window算子实现逻辑，统计window中元组的个数
     *
     * @param tuple
     * @param window
     * @param input
     * @param out
     * @throws Exception
     */
    @Override
    public void apply(Tuple tuple, TimeWindow window, Iterable<Tuple4<Long, String, String, Integer>> input, Collector<Long> out) throws Exception {
        long count = 0L;
        for (Tuple4<Long, String, String, Integer> event : input) {
            count++;
        }
        total += count;
        out.collect(count);
    }
}
