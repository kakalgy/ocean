package com.yusj.seal.io;

import java.io.IOException;

/**
 * OutputStream：字节输出流、同InputStream意义相同、本身是一个抽象类、为所有字节输出流提供一个标准、和一些简单的方法及简单实现。
 * 其核心方法是一个抽象方法: write(byte b)要求子类必须实现此方法、不同功能的类对此方法的实现方式不一样、一般情况下子类要重写其大多数方法、或者新增一些方法、用于满足更多的需要。
 */
public abstract class OutputStream implements Closeable, Flushable {

    /**
     * 将一个8位的字节写入到当前字节输出流中。其子类必须重写、实现此方法。
     *
     * @param b 参数为int类型，32位，只取低位的8位，其余24位忽略
     * @throws IOException
     */
    public abstract void write(int b) throws IOException;

    /**
     * 将字节数组中b.length个字节写入到当前输出流中。此方法相当与write(byte[] b, 0, b.length);因为其内部就是调用下面的方法。
     *
     * @param b
     * @throws IOException
     */
    public void write(byte b[]) throws IOException {
        write(b, 0, b.length);
    }

    /**
     * 将一个字节数组中从下标off开始、len个字节写入到当前输出流中。不断调用write来实现的。
     *
     * @param b
     * @param off
     * @param len
     * @throws IOException
     */
    public void write(byte b[], int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        for (int i = 0; i < len; i++) {
            write(b[off + i]);
        }
    }

    /**
     * 关闭当前输出流、释放所有与此流有关的资源。
     *
     * @throws IOException
     */
    public void close() throws IOException {

    }

    /**
     * 将当前输出流中的所有残留数据刷新到目的地中去。
     *
     * @throws IOException
     */
    public void flush() throws IOException {

    }
}
