package com.yusj.starfish.batch;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.IterativeDataSet;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;

public class PageRankBatch {
    public static void main(String[] args) {
        int maxIteration = 0;

        try {
            final ParameterTool params = ParameterTool.fromArgs(args);
            maxIteration = params.has("maxIteration") ? Integer.parseInt(params.get("maxIteration")) : 3;
//            port = params.getInt("port");
        } catch (Exception e) {
            System.err.println("No port specified. Please run 'SocketWindowWordCount "
                    + "--hostname <hostname> --port <port>', where hostname (localhost by default) "
                    + "and port is the address of the text server");
            System.err.println(
                    "To start a simple text server, run 'netcat -l <port>' and " + "type the input text into the command line");

        }

        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        DataSet<Tuple2<Long, Double>> pagesWithRanks = env.readCsvFile("/home/example/batch/pages.csv")
                .types(Long.class, Double.class);

//        DataSet<Tuple2<Long,Double>> pageLinkLists =

        IterativeDataSet<Tuple2<Long, Double>> iteration = pagesWithRanks.iterate(maxIteration);
    }
}
