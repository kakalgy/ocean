package com.yusj.seal.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 为传入的基础输出流（以下简称 out）提供缓冲功能、先将要写入out的数据写入到BufferedOutputStream缓冲数组buf中、
 * 当调用bufferedOutputStream的flush()时、或者调用flushBuffer()时一次性的将数据使用out.write(byte[]b , 0, count)写入out指定的目的地中、
 * 避免out.write(byte b)方法每写一个字节就访问一次目的地。
 */
public class BufferedOutputStream extends FilterOutputStream {
    /**
     * BufferedOutputStream内置的缓存数组、当out进行写入时、BufferedOutputStream会先将out写入的数据
     * 放到buf中、
     * 注意：不一定buf[]被写满之后才会写入out中。
     */
    protected byte buf[];
    /**
     * 缓存数组中现有的可供写入的有效字节数、
     * 注意：这里是buf中的、而不是out中的。
     */
    protected int count;

    /**
     * 使用BufferedOutputStream默认的缓存数组大小与基础输出流out创建BufferedOutputStream。
     */
    public BufferedOutputStream(OutputStream out) {
        this(out, 8192);
    }

    /**
     * 使用指定的的缓存数组大小与基础输出流out创建BufferedOutputStream。
     */
    public BufferedOutputStream(OutputStream out, int size) {
        super(out);
        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        buf = new byte[size];
    }

    /**
     * 如果buf中有的数据、则立刻将buf中所有数据写入目的地中。 并且将count重置为0。
     */
    private void flushBuffer() throws IOException {
        if (count > 0) {
            out.write(buf, 0, count);
            count = 0;
        }
    }

    /**
     * 将要写入out中的数据先写入到buf中。
     */
    public synchronized void write(int b) throws IOException {
        if (count >= buf.length) {
            flushBuffer();
        }
        buf[count++] = (byte) b;
    }

    /**
     * 将从下标off开始、长度为len个字节的byte[]b写入buf中、
     */
    public synchronized void write(byte b[], int off, int len) throws IOException {
        if (len >= buf.length) {
            /* If the request length exceeds the size of the output buffer,
               flush the output buffer and then write the data directly.
               In this way buffered streams will cascade harmlessly. */
            /* 如果写入的字节个数超过buf的长度、
             * 那么直接调用out.write(byte b[] , int off, int len)避免重复操作、即读到buf中但buf立马装满
             * 此时将buf写入out中又将剩下的填充到buf中。
             */
            flushBuffer();
            out.write(b, off, len);
            return;
        }
        //如果写入的个数大于buf中现有剩余空间、则将buf中的现有count个字节写入out中、注意：flushBuffer之后、count变成了0。
        //再将len个字节写入从count下标开始长度为len的buf中。
        //同时将count 变成 count +=len.
        if (len > buf.length - count) {
            flushBuffer();
        }
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }

    /**
     * 1）将buf中现有字节写入out中
     * 2）将out中的现有字节flush到目的地中。
     */
    public synchronized void flush() throws IOException {
        flushBuffer();
        out.flush();
    }
}
