package com.yusj.starfish.checkpoints.source;

import com.yusj.starfish.checkpoints.state.UDFState;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.checkpoint.ListCheckpointed;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 带checkpoint的数据源
 */
public class EventSourceWithChk extends RichSourceFunction<Tuple4<Long, String, String, Integer>> implements ListCheckpointed<UDFState> {

    private Long count = 0L;
    private boolean isRunning = true;
    private String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWZYX0987654321";

    /**
     * 算子的主要逻辑，每秒钟向流图中注入10000个元组
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void run(SourceContext<Tuple4<Long, String, String, Integer>> ctx) throws Exception {
        Random random = new Random();
        while (isRunning) {
            for (int i = 0; i < 1; i++) {
                ctx.collect(Tuple4.of(random.nextLong(), "hello-" + count, alphabet, 1));
                count++;
            }
            Thread.sleep(10000);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }

    /**
     * 制作自定义快照
     *
     * @param checkpointId
     * @param timestamp
     * @return
     * @throws Exception
     */
    @Override
    public List<UDFState> snapshotState(long checkpointId, long timestamp) throws Exception {
        UDFState udfState = new UDFState();
        List<UDFState> listState = new ArrayList<UDFState>();
        udfState.setState(count);
        System.out.println("EventSourceWithChk.snapshotState: " + count);
        listState.add(udfState);
        return listState;
    }

    /**
     * 从自定义快照中恢复数据
     *
     * @param state
     * @throws Exception
     */
    @Override
    public void restoreState(List<UDFState> state) throws Exception {
        UDFState udfState = state.get(0);
        count = udfState.getState();
        System.out.println("EventSourceWithChk.restoreState: " + count);
    }
}
