package com.yusj.seal.io;

import java.io.IOException;

/**
 * 一个字节数组包含一个内置缓存数组、该缓存数组中的字段就是供ByteArrayInputStream读取到程序中的流。
 */
public class ByteArrayInputStream extends InputStream {

    /**
     * 构造方法传入的一个字节数组、作为数据源、read()读取的第一个字节应该是
     * 这个数组的第一个字节、即buf[0]、下一个是buf[pos];
     */
    protected byte buf[]; //内置缓存数组

    /**
     * 下一个从输入流缓冲区中读取的字节的下标、不能为负数、
     * 下一个读入的字节应该是buf[pos];
     */
    protected int pos;//buf中下一个被读取的字节下标

    /**
     * 当前输入流的当前的标记位置、默认是开始、即下标是0
     */
    protected int mark = 0;//标记当前流读取下一个字节的位置、默认是起始下标。

    /**
     * 注意：这里count不代表buf中可读字节总数！
     * <p>
     * 此值应该始终是非负数，并且不应大于 buf 的长度。
     * 它比 buf 中最后一个可从输入流缓冲区中读取的字节位置大一。
     */
    protected int count;//buf中有效可读字节总数

    /**
     * 构造函数
     * <p>
     * 创建一个使用buf[]作为其缓冲区数组的ByteArrayInputStream、
     * 这个buf[]不是复制来的、并且给pos、count赋初始值。
     * 传入的是作为源的字节数组。
     * 赋给的是作为缓存区的字节数组。
     *
     * @param buf
     */
    public ByteArrayInputStream(byte buf[]) {
        this.buf = buf;
        this.pos = 0;
        this.count = buf.length;
    }

    /**
     * 构造函数
     * <p>
     * 创建一个使用buf[]的起始下标offset，长度为length的字节数组作为其缓冲区数组的ByteArrayInputStream、
     * * 这个buf[]不是复制来的、并且给pos、count赋初始值。
     * * 传入的是作为源的字节数组。
     * * 赋给的是作为缓存区的字节数组。
     * * 将偏移量设为offset
     *
     * @param buf
     * @param offset
     * @param length
     */
    public ByteArrayInputStream(byte buf[], int offset, int length) {
        this.buf = buf;
        this.pos = offset;
        //初始化buf的长度、取min的意义在于剔除不合理参数、避免出现异常、
        this.count = Math.min(offset + length, buf.length);
        this.mark = offset;
    }

    /**
     * 开始读取源字节数组中的字节、并将字节以整数形式返回
     * 返回值在 0 到 255 之间、当读到最后一个的下一个时返回 -1.
     *
     * @return
     */
    @Override
    public synchronized int read() {

        return (pos < count) ? (buf[pos++] & 0xff) : -1;
    }

    /**
     * 从源字节数中下标为pos开始的len个字节读取到 b[]的从下标off开始长度为len的字节数组中。
     * * 同时将下一个将要被读取的字节下标设置成pos+len。这样当循环读取的时候就可以读取源中所有的字节。
     * * 返回读取字节的个数。
     * * 当读到最后一个的下一个时返回 -1
     *
     * @param b   用来存放读取的字节的字节缓存数组
     * @param off 从b[off]开始放
     * @param len 放len个
     * @return
     */
    public synchronized int read(byte b[], int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }

        //如果buf中没有字节可供读取、返回-1；
        if (pos >= count) {
            return -1;
        }

        int avail = count - pos;
        //如果buf中剩余字节不够len个、返回count-pos；这个值是作为实际读取字节数返回的
        if (len > avail) {
            len = avail;
        }

        //如果传入的要求读取的字节数小于0、则返回0。
        if (len <= 0) {
            return 0;
        }

        //将buf 下标为pos的之后的字节数组复制到b中下标从b开始长度为len的数组中
        System.arraycopy(buf, pos, b, off, len);

        //设置下一个被读取字节的下标。
        pos += len;
        return len;
    }

    /**
     * 返回实际跳过的字节个数、同时将pos后移  “ 实际跳跃的字节数” 位
     * 下次再读的时候就从pos开始读取。那此时就会忽略被跳过的字节。
     *
     * @param n 我们想从从输入流中跳过的字节数
     * @return
     */
    public synchronized long skip(long n) {
        //如果要跳过的字节数大于剩余字节数、那么实际跳过的字节数就是剩余字节数
        long k = count - pos;
        if (n < k) {
            k = n < 0 ? 0 : n;
        }
        //最后设置偏移量、即下一个被读取字节的下标。
        pos += k;
        return k;
    }

    /**
     * 返回当前字节数组输入流实际上还有多少能被读取的字节个数。
     *
     * @return
     */
    public synchronized int available() {

        return count - pos;
    }

    /**
     * 查看此输入流是否支持mark
     *
     * @return
     */
    public boolean markSupported() {

        return true;
    }

    /**
     * 设置当前流的mark位置、
     * * 值得注意的是 readAheadLimit这个参数无效
     * * 只是把pos的当前值传给mark。
     * * mark取决于创建ByteArrayStream时传入的byte[]b, int offset ...中的offset、若未使用这个构造方法、则取默认值0。
     *
     * @param readAheadLimit
     */
    public void mark(int readAheadLimit) {

        mark = pos;
    }

    /**
     * 将mark的值赋给pos、使流可以从此处继续读取。下次读取的时候还是从pos下标开始读取
     * * 也就是继续接着上次被mark的地方进行读取。
     */
    public synchronized void reset() {

        pos = mark;
    }

    /**
     * 关闭此流、释放所有与此流有关的资源。调用此方法没有任何效果
     *
     * @throws IOException
     */
    public void close() throws IOException {
    }
}
