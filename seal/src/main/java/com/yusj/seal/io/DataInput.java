package com.yusj.seal.io;

import java.io.IOException;

/**
 * @Description
 * @Author kakalgy
 * @Date 2018/10/10 23:55
 **/
public interface DataInput {
    void readFully(byte b[]) throws IOException;

    void readFully(byte b[], int off, int len) throws IOException;

    int skipBytes(int n) throws IOException;

    boolean readBoolean() throws IOException;

    byte readByte() throws IOException;

    int readUnsignedByte() throws IOException;

    short readShort() throws IOException;

    int readUnsignedShort() throws IOException;

    char readChar() throws IOException;

    int readInt() throws IOException;

    long readLong() throws IOException;

    float readFloat() throws IOException;

    double readDouble() throws IOException;

    String readLine() throws IOException;

    String readUTF() throws IOException;
}
