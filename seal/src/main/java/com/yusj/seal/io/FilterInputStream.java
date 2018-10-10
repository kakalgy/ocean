package com.yusj.seal.io;

import java.io.IOException;

/**
 * FileterInputStream 过滤器字节输入流    、本身什么事都没有做、只是简单的重写InputStream的所有方法。
 * @Author kakalgy
 * @Date 2018/10/10 23:19
 **/
public class FilterInputStream extends InputStream {
    /**
     * 接收构造函数传递进来的 InputStream具体实现类。
     * 至于 volatile 关键字的意义 参见：http://blog.csdn.net/crave_shy/article/details/14610155
     */
    protected volatile InputStream in;
    /**
     * 根据传入的InputStream具体实现类创建FilterInputStream、并将此实现类赋给全局变量 in、方便重写此实现类从InputStream继承的或重写的所有方法。
     */
    protected FilterInputStream(InputStream in) {
        this.in = in;
    }
    /**
     * 具体InputStream实现类的read()方法、若子类自己实现的、则是子类的、若子类没有实现则用的是InputStream本身的方法。
     * 下面的方法一样。若有不明白的可见前面的InputStream源码分析。
     */
    public int read() throws IOException {
        return in.read();
    }

    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte b[], int off, int len) throws IOException {
        return in.read(b, off, len);
    }

    public long skip(long n) throws IOException {
        return in.skip(n);
    }

    public int available() throws IOException {
        return in.available();
    }

    public void close() throws IOException {
        in.close();
    }

    public synchronized void mark(int readlimit) {
        in.mark(readlimit);
    }

    public synchronized void reset() throws IOException {
        in.reset();
    }

    public boolean markSupported() {
        return in.markSupported();
    }
}
