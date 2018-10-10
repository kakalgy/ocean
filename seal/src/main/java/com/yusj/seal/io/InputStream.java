package com.yusj.seal.io;

import java.io.IOException;

/**
 * 所有字节输出流的父抽象类。提供字节输入流所共有的基本的读取方法。
 * <p>
 * <p/>
 * InputStream：字节输入流、是所有字节输入流的父类、本身个抽象类、为字节输入流提供一个标准、和基本的方法及简单的实现、子类可以根据自己的特点进行重写和扩
 * 展。InputStream中有一个抽象方法read()、是字节输入流的核心、要求子类必须实现此方法、此方法也是字节输入流的核心方法
 */
public abstract class InputStream implements Closeable {

    /**
     * skipBuffer的大小
     */
    private static final int MAX_SKIP_BUFFER_SIZE = 2048;

    /**
     * 抽象的方法 用于读取输入流中的下一个字节、由其实现类去重写。这也是流的关键方法、下面几个方法都是直接或者间接调用read()方法来实现。
     *
     * @return
     * @throws IOException
     */
    public abstract int read() throws IOException;

    /**
     * 从方法实现也可看出、他是不断调用read(byte[] b, 0, b.length)这个方法作为其方法体。
     *
     * @param b 作为一个缓存字节数组来存放从流中读取的字节。
     * @return 读取的字节总个数、如果读到结尾、则返回-1.
     * @throws IOException
     */
    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * 不断的调用read()方法来读取流中的字节、存放到b 中。
     *
     * @param b   用来存放读取的字节的字节缓存数组
     * @param off 从b[off]开始放
     * @param len 放len个
     * @return 返回读取的字节的总个数。
     * @throws IOException
     */
    public int read(byte b[], int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }

        int c = read();
        if (c == -1) {
            return -1;
        }
        b[off] = (byte) c;

        int i = 1;
        try {
            for (; i < len; i++) {
                c = read();
                if (c == -1) {
                    break;
                }
                b[off + i] = (byte) c;
            }
        } catch (IOException ee) {
        }
        return i;
    }

    /**
     * 返回跳过的字节数的个数。
     * * 这里根本就看不出这个方法到底有什么实际的用途、但是当看到后面的子类实现类的方法体就知道了。
     * </p>方法思想：实际抛弃的字节数  = 想要抛弃的字节数  - 剩余需要抛弃的字节数
     *
     * @param n 我们想从从输入流中跳过的字节数
     * @return 实际跳过的字节数、因为有可能输入流中没有那么多有效字节被抛弃、此时则会跳过剩余所有字节、返回的字节数也就是跳之前剩余的字节数
     * @throws IOException
     */
    public long skip(long n) throws IOException {

        //记录还剩余多少字节要跳过。
        long remaining = n;
        //中间变量、记录每次读取的实际字节数
        int nr;

        if (n <= 0) {
            return 0;
        }

        int size = (int) Math.min(MAX_SKIP_BUFFER_SIZE, remaining);
        //创建临时的用于存放被读取的字节的字节数组、像垃圾桶、回收被跳过的字节、满了之后在读取就清空从新回收。
        byte[] skipBuffer = new byte[size];

        //此循环意义在于记录剩余需要抛弃的字节数,大条件、如果还有字节需要被跳过、也就是抛弃、没有则结束循环
        while (remaining > 0) {
            //记录此次实际读取的字节数、后面参数的意义在于防止出现IndexOutOfBoundsException异常。
            nr = read(skipBuffer, 0, (int) Math.min(size, remaining));
            //如果读到输入流结尾、则结束循环。
            if (nr < 0) {
                break;
            }
            //修改记录还要抛弃的字节数的值
            remaining -= nr;
        }
        //将n减去还剩多少需要被抛弃的值就是实际抛弃的值
        return n - remaining;
    }

    /**
     * 返回输入流中可读取的有效的字节数、若子类提供此功能、则需要重新实现。
     *
     * @return
     * @throws IOException
     */
    public int available() throws IOException {
        return 0;
    }

    /**
     * 关闭流、释放所有与此流有关的资源。
     *
     * @throws IOException
     */
    public void close() throws IOException {

    }

    /**
     * 在输入流中标记当前位置、与reset()结合使用、
     * 若子类不提供此功能则调用此方法没有任何效果。
     * 若子类提供此功能、则需重新实现。
     *
     * @param readlimit
     */
    public synchronized void mark(int readlimit) {

    }

    /**
     * 与mark(int readlimit)结合使用、将此流定位到最后一次mark的位置、即调用reset之后、程序继续从mark的位置读取字节流。
     * 若子类不支持此方法、调用则会抛出异常、可事先调用下面的markSupported()来查看当前流是否支持此功能。
     *
     * @throws IOException
     */
    public synchronized void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }

    /**
     * 查看当前流是否支持mark、默认是false、即如果实现类不重写此方法就意味着其不支持mark。
     *
     * @return
     */
    public boolean markSupported() {

        return false;
    }
}
