package com.yusj.starfish.source;

import org.apache.flink.api.java.utils.ParameterTool;

public class SocketSource {
    private String hostName;
    private int port;

    public SocketSource(String[] args) {
        // the host and the port to connect to
//        final String hostname;
//        final int port;
        try {
            final ParameterTool params = ParameterTool.fromArgs(args);
            hostName = params.has("hostname") ? params.get("hostname") : "localhost";
            port = params.getInt("port");
        } catch (Exception e) {
            System.err.println("No port specified. Please run 'SocketWindowWordCount "
                    + "--hostname <hostname> --port <port>', where hostname (localhost by default) "
                    + "and port is the address of the text server");
            System.err.println(
                    "To start a simple text server, run 'netcat -l <port>' and " + "type the input text into the command line");

        }
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


}
