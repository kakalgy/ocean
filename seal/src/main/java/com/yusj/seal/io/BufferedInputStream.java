package com.yusj.seal.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * BufferedInputStream 缓冲字节输入流、作为FilterInputStream的一个实现类、为子类添加缓冲功能、自带一个缓冲数组、
 * 是为传入的InputStream的实现类（简称in）提供缓冲读取功能、他的原理是从in中一次性读取一个数据块放入自带的缓冲数组中、
 * 当这个缓冲数组中的数据被读取完时、BufferedInputStream再从in中读取一个数据块、这样循环读取。
 * 缓存数组是放在内存中的、当程序从buf中读取字节的时候、相当于从内存中读取、从内存中读取的效率至少是从磁盘等存储介质的十倍以上！
 * 这里会有人想干嘛不一次性读取所有in中的字节放入内存中？
 * 1）读取全部字节可能耗费的时间较长、
 * 2）内存有限、不想磁盘等存储介质存储空间那么大、价格也相对昂贵的多！
 */
public class BufferedInputStream extends FilterInputStream {
    private static int DEFAULT_BUFFER_SIZE = 8192;

    /**
     * BufferedInputStream自带的数组大小
     */
    private static int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
    /**
     * 自带数组、如果在根据传入的in构造BufferedInputStream没有传入时、其大小为8192、
     * 如果传入则使用传入的大小。
     */
    protected volatile byte buf[];
    /**
     * 缓存数组的原子更新器。
     * 该成员变量与buf数组的volatile关键字共同组成了buf数组的原子更新功能实现，
     * 即，在多线程中操作BufferedInputStream对象时，buf和bufUpdater都具有原子性(不同的线程访问到的数据都是相同的)
     */
    private static final
    AtomicReferenceFieldUpdater<java.io.BufferedInputStream, byte[]> bufUpdater =
            AtomicReferenceFieldUpdater.newUpdater
                    (java.io.BufferedInputStream.class, byte[].class, "buf");
    /**
     * 当前缓冲字节数组中的有效可供读取的字节总数。
     * 注意这里是缓冲区、即缓冲数组buf中的有效字节、而不是in中有效的字节
     */
    protected int count;
    /**
     * 用于标记buf中下一个被读取的字节的下标、即下一个被读取的字节是buf[pos]、pos取值范围一般是 0 到count、
     * 如果pos = count则说明这个buf中的字节被读取完、那么需要从in中读取下一个数据块用于读取。
     * 同样：这里指的是缓冲字节数组中字节的下标、而不是in中的字节的下标
     */
    protected int pos;
    /**
     * 当前缓冲区的标记位置、
     * mark() reset()结合使用步骤（单独使用没有意义）
     * 1）调用mark()、将标记当前buf中下一个要被读取的字节的索引 pos保存到 markpos中
     * 2）调用reset()、将markpos的值赋给pos、这样再次调用read()的时候就会从最后一次调用mark方法的位置继续读取。
     */
    protected int markpos = -1;
    /**
     * 调用mark方法后、在后续调用reset()方法失败之前所允许的最大提前读取量。
     */
    protected int marklimit;

    /**
     * 检测传入的基础流in是否关闭。若没有关闭则返回此流。
     */
    private java.io.InputStream getInIfOpen() throws IOException {
        InputStream input = in;
        if (input == null)
            throw new IOException("Stream closed");
        return input;
    }

    /**
     * 检测BufferedInputStream是否关闭、如果没有关闭则返回其自带缓存数组buf。
     */
    private byte[] getBufIfOpen() throws IOException {
        byte[] buffer = buf;
        if (buffer == null)
            throw new IOException("Stream closed");
        return buffer;
    }

    /**
     * 使用BufferedInputStream默认的缓冲字节数组大小及传入的基础输入字节流in创建BufferedInputStream。
     */
    public BufferedInputStream(InputStream in) {
        this(in, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 使用传入的缓冲字节数组大小及传入的基础输入字节流in创建BufferedInputStream。
     */
    public BufferedInputStream(InputStream in, int size) {
        super(in);
        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        buf = new byte[size];
    }

    /**
     * 从输入流in中获取字节、填充到缓冲字节数组中。这里由于时间关系没有过于纠结内部实现、有兴趣的可以自己研究下
     */
    private void fill() throws IOException {
        byte[] buffer = getBufIfOpen();
        if (markpos < 0)
            pos = 0;            /* no mark: throw away the buffer */
        else if (pos >= buffer.length)  /* no room left in buffer */
            if (markpos > 0) {  /* can throw away early part of the buffer */
                int sz = pos - markpos;
                System.arraycopy(buffer, markpos, buffer, 0, sz);
                pos = sz;
                markpos = 0;
            } else if (buffer.length >= marklimit) {
                markpos = -1;   /* buffer got too big, invalidate mark */
                pos = 0;        /* drop buffer contents */
            } else if (buffer.length >= MAX_BUFFER_SIZE) {
                throw new OutOfMemoryError("Required array size too large");
            } else {            /* grow buffer */
                int nsz = (pos <= MAX_BUFFER_SIZE - pos) ?
                        pos * 2 : MAX_BUFFER_SIZE;
                if (nsz > marklimit)
                    nsz = marklimit;
                byte nbuf[] = new byte[nsz];
                System.arraycopy(buffer, 0, nbuf, 0, pos);
                //由于内部原因无法生成代码
//                if (!bufUpdater.compareAndSet(this, buffer, nbuf)) {
//                    // Can't replace buf if there was an async close.
//                    // Note: This would need to be changed if fill()
//                    // is ever made accessible to multiple threads.
//                    // But for now, the only way CAS can fail is via close.
//                    // assert buf == null;
//                    throw new IOException("Stream closed");
//                }
                buffer = nbuf;
            }
        count = pos;
        int n = getInIfOpen().read(buffer, pos, buffer.length - pos);
        if (n > 0)
            count = n + pos;//根据从输入流中读取的实际数据的多少，来更新buffer中数据的实际大小
    }

    /**
     * 读取下一个字节、并以整数形式返回。
     */
    public synchronized int read() throws IOException {
        if (pos >= count) {
            // 若已经读完缓冲区中的数据，则调用fill()从输入流读取下一部分数据来填充缓冲区
            fill();
            //读取完in最后一个字节。
            if (pos >= count)
                return -1;
        }
        return getBufIfOpen()[pos++] & 0xff;
    }

    /**
     * 将缓存字节数组中的字节写入到从下标off开始、长度为len的byte[]b 中。
     */
    private int read1(byte[] b, int off, int len) throws IOException {
        int avail = count - pos;
        if (avail <= 0) {
            /* If the requested length is at least as large as the buffer, and
               if there is no mark/reset activity, do not bother to copy the
               bytes into the local buffer.  In this way buffered streams will
               cascade harmlessly. */
            /**
             * 如果传入的byte[]的长度len大于buf的size、并且没有markpos、就直接从原始流中读取len个字节放入byte[]b中
             * 避免不必要的copy：从原始流中读取buf.length个放入buf中、再将buf中的所有字节放入byte[]b 中
             * 再清空buf、再读取buf.length个放入buf中 、还要放入byte[]b中直到 len个。
             */
            if (len >= getBufIfOpen().length && markpos < 0) {
                return getInIfOpen().read(b, off, len);
            }
            //若缓冲区数据被读取完、则调用fill()填充buf
            fill();
            avail = count - pos;
            if (avail <= 0) return -1;
        }
        int cnt = (avail < len) ? avail : len;
        System.arraycopy(getBufIfOpen(), pos, b, off, cnt);
        pos += cnt;
        return cnt;
    }

    /**
     * 将buf中的字节读取到下标从off开始、len长的byte[]b 中
     */
    public synchronized int read(byte b[], int off, int len)
            throws IOException {
        getBufIfOpen(); // Check for closed stream
        if ((off | len | (off + len) | (b.length - (off + len))) < 0) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
/**
 * 不断的读取字节、放入b中、有以下几种情况
 * 1）buf中有len个可供读取、则将len个字节copy到b中
 * 2）buf中不足len个、从源输入流中读取下一个数据块
 * 	  a）源输入流中有可供读取的有效的、读取并填充buf、继续填充到b中
 * 	  b）读到结尾、依然不够、将所有读取的填充到b中
 */
        int n = 0;
        for (; ; ) {
            int nread = read1(b, off + n, len - n);
            if (nread <= 0)
                return (n == 0) ? nread : n;
            n += nread;
            if (n >= len)
                return n;
            // if not closed but no bytes available, return
            InputStream input = in;
            if (input != null && input.available() <= 0)
                return n;
        }
    }

    /**
     * 跳过n个字节、返回实际跳过的字节数。
     */
    public synchronized long skip(long n) throws IOException {
        getBufIfOpen(); // Check for closed stream
        if (n <= 0) {
            return 0;
        }
        long avail = count - pos;

        if (avail <= 0) {
            // If no mark position set then don't keep in buffer
            if (markpos < 0)
                return getInIfOpen().skip(n);

            // Fill in buffer to save bytes for reset
            fill();
            avail = count - pos;
            if (avail <= 0)
                return 0;
        }

        long skipped = (avail < n) ? avail : n;
        pos += skipped;
        return skipped;
    }

    /**
     * 返回in中有效可供被读取的字节数。从这里也可以看出、有效字节是in中现有有效字节与buf中剩余字节的和。
     * 即buf中是从in中读取的一个供程序读取的数据块。
     */
    public synchronized int available() throws IOException {
        int n = count - pos;
        int avail = getInIfOpen().available();
        return n > (Integer.MAX_VALUE - avail)
                ? Integer.MAX_VALUE
                : n + avail;
    }

    /**
     * @param readlimit 在mark方法方法失效前最大允许读取量
     */
    public synchronized void mark(int readlimit) {
        marklimit = readlimit;
        markpos = pos;
    }

    /**
     * 将最后一次调用mark标记的位置传递给pos、使得此流可以继续接着最后一次mark的地方读取。
     * 如果没有调用mark、或者mark失效则抛出IOException。
     */
    public synchronized void reset() throws IOException {
        getBufIfOpen(); // Cause exception if closed
        if (markpos < 0)
            throw new IOException("Resetting to invalid mark");
        pos = markpos;
    }

    /**
     * 查看此流是否支持markSupport
     *
     * @return true 此流支持mark。
     */
    public boolean markSupported() {
        return true;
    }

    /**
     * 关闭此流并释放所有资源。
     */
    public void close() throws IOException {
        byte[] buffer;
        while ((buffer = buf) != null) {
            //由于内部原因无法生成代码
//            if (bufUpdater.compareAndSet(this, buffer, null)) {
//                InputStream input = in;
//                in = null;
//                if (input != null)
//                    input.close();
//                return;
//            }
            // Else retry in case a new buf was CASed in fill()
        }
    }
}
