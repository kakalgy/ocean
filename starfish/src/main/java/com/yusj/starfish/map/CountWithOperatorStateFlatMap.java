package com.yusj.starfish.map;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

public class CountWithOperatorStateFlatMap extends RichFlatMapFunction<Long, String> implements CheckpointedFunction {

    private transient ListState<Long> checkPointCountList;
    private List<Long> listElements;

    public CountWithOperatorStateFlatMap() {
        System.out.println("CountWithOperatorStateFlatMap Constructor");
        this.listElements = new ArrayList<>();
    }

    @Override
    public void flatMap(Long input, Collector<String> collector) throws Exception {
        if (input == 1) {
            if (listElements.size() > 0) {
                StringBuffer stringBuffer = new StringBuffer();
                for (Long i : listElements) {
                    stringBuffer.append(i).append(" ");
                }
                collector.collect(stringBuffer.toString());
                listElements.clear();
                System.out.println(stringBuffer.toString());
            }
        } else {
            listElements.add(input);
        }
    }

    public void snapshotState(FunctionSnapshotContext functionSnapshotContext) throws Exception {
        System.out.println("####snapshotState start####");
        checkPointCountList.clear();
        for (Long i : listElements) {
            System.out.print(i + ", ");
            checkPointCountList.add(i);
        }
        System.out.println();
        System.out.println("####snapshotState End####");
    }

    public void initializeState(FunctionInitializationContext functionInitializationContext) throws Exception {
        System.out.println("%%%%initializeState start%%%%");
        ListStateDescriptor<Long> listStateDescriptor = new ListStateDescriptor<Long>("countForOperator",
                TypeInformation.of(new TypeHint<Long>() {
                }));

        checkPointCountList = functionInitializationContext.getOperatorStateStore().getListState(listStateDescriptor);

        if (functionInitializationContext.isRestored()) {
            for (Long i : checkPointCountList.get()) {
                System.out.println(i);
                listElements.add(i);
            }
        }

        System.out.println("%%%%initializeState end%%%%");
        System.out.println();

    }
}
