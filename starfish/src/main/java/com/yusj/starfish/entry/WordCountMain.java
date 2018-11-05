package com.yusj.starfish.entry;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

public class WordCountMain {
    public static void main(String[] args) throws Exception {

        // the host and the port to connect to
        final String hostname;
        final int port;
        try {
            final ParameterTool params = ParameterTool.fromArgs(args);
            hostname = params.has("hostname") ? params.get("hostname") : "localhost";
            port = params.getInt("port");
        } catch (Exception e) {
            System.err.println("No port specified. Please run 'SocketWindowWordCount "
                    + "--hostname <hostname> --port <port>', where hostname (localhost by default) "
                    + "and port is the address of the text server");
            System.err.println(
                    "To start a simple text server, run 'netcat -l <port>' and " + "type the input text into the command line");
            return;
        }

        // get the execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        StreamTableEnvironment tEnv = TableEnvironment.getTableEnvironment(env);

//        JDBCAppendTableSink sink = JDBCAppendTableSink.builder().setDrivername("com.mysql.jdbc.Driver")
//                .setDBUrl("jdbc:mysql://10.180.139.184:3306/sandbox?characterEncoding=UTF-8").setUsername("hillstone")
//                .setPassword("hillstone").setQuery("INSERT INTO test (word,count) VALUES (?,?)")
//                .setParameterTypes(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO).build();

        // get input data by connecting to the socket
        DataStream<String> text = env.socketTextStream(hostname, port, "\n");

        // parse the data, group it, window it, and aggregate the counts

        DataStream<Tuple2<String, Integer>> windowCounts = text

                .flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {

                    public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
                        for (String word : value.split("\\s")) {
                            out.collect(new Tuple2<>(word, 1));
                            // System.out.println(word);
                        }
                    }
                }).keyBy(0).timeWindow(Time.seconds(30)).reduce(new ReduceFunction<Tuple2<String, Integer>>() {
                    public Tuple2<String, Integer> reduce(Tuple2<String, Integer> a, Tuple2<String, Integer> b) {
                        return new Tuple2<String, Integer>(a.f0, a.f1 + b.f1);
                    }
                })
                // .addSink(new SinkFunction<Tuple2<String, Integer>>() {
                // @Override
                // public void invoke(Tuple2<String, Integer> value) throws Exception {
                // tEnv.fromDataStream(value, "word, count").writeToSink(sink);
                // }
                // })
                ;

//        Table table = tEnv.fromDataStream(windowCounts, "word, count");
//        table.writeToSink(sink);

//         print the results with a single thread, rather than in parallel
        windowCounts.print().setParallelism(1);

        env.execute("Socket Window WordCount");
    }

    // ------------------------------------------------------------------------

    /**
     * Data type for words with count.
     */
    public static class WordWithCount {

        public String word;
        public long count;

        public WordWithCount() {
        }

        public WordWithCount(String word, long count) {
            this.word = word;
            this.count = count;
        }

        @Override
        public String toString() {
            return word + " : " + count;
        }
    }
}
