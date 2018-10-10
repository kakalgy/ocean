package com.yusj.seal.io;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * 字节数组输出流、用于将字节或者字节数组写入到ByteArrayOutputStream（以下简称bos）内置缓存字节数组buf中、这里buf可以看做是bos的目的地、与其他流不同的是
 * 我们可以对buf操作、比如将它转换为String到程序中、或者直接作为一个字节数组将其写入到另一个低级字节数组输出流中、同样close()方法对此流也没有作用、当调用
 * bos.close()关闭此流时、bos的其他方法仍然能正常使用、并且不会抛出IOException异常。
 *
 * @Param
 * @return
 **/
public class ByteArrayOutputStream extends OutputStream {
    /**
     * 存储字节数组的缓存字节数组、存放在内存中.
     */
    protected byte buf[];//内置缓存字节数组、用于存放写入的字节

    /**
     * 缓存字节数组中有效字节数
     */
    protected int count;//缓存字节数组buf中的字节数

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * 构造函数<p/>
     * 创建一个带有初始大小为32字节的字节缓存字节数组的字节数组输出流。
     * 当缓存字节数组存满时会自动增加一倍容量
     **/
    public ByteArrayOutputStream() {
        //默认缓存字节数组大小为32位。
        this(32);
    }

    /**
     * 构造函数<p/>
     * 创建一个指定初始大小的字节数组输出流。
     * 参数不能为负。
     **/
    public ByteArrayOutputStream(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative initial size: " + size);
        }
        //将传入的size设为缓存字节数组的大小
        buf = new byte[size];
    }

    private void ensureCapacity(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - buf.length > 0)
            grow(minCapacity);
    }

    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = buf.length;
        int newCapacity = oldCapacity << 1;
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        buf = Arrays.copyOf(buf, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }

    /**
     * 将一个指定的字节写入到字节数组输出流中、存放在字节数组输出流的缓存字节数组即buf中。
     * 缓存字节数组满时会自动扩容。
     */
    @Override
    public synchronized void write(int b) {
        ensureCapacity(count + 1);
        buf[count] = (byte) b;
        count += 1;
    }

    /**
     * 将一个指定的字节数组从下标off开始len长个字节写入到缓存字节数组中。
     * 缓存字节数组满时会自动扩容。
     */
    public synchronized void write(byte b[], int off, int len) {
        if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) - b.length > 0)) {
            throw new IndexOutOfBoundsException();
        }
        ensureCapacity(count + len);
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }

    /**
     * 调用另一个OutputStream 的 write(byte[] b, 0, count);方法、将此字节数组输出流的缓存字节数组中的全部字节写入到
     * 另一个OutputStream指定的目的地中去。
     */
    public synchronized void writeTo(OutputStream out) throws IOException {
        out.write(buf, 0, count);
    }

    /**
     * 重置指定字节写入缓存字节数组的位置、使后面写入的字节重新从缓存字节数组buf的下标0开始写入到缓存字节数组buf中。
     */
    public synchronized void reset() {

        count = 0;
    }

    /**
     * 将此输出流的缓存字节数组转换成程序中指定代表此流中所有字节的字节数组。
     */
    public synchronized byte toByteArray()[] {
        return Arrays.copyOf(buf, count);
    }

    /**
     * 返回此字节数组输出流的缓存字节数组的size、即其中的字节的个数
     */
    public synchronized int size() {
        return count;
    }

    /**
     * 将当前缓存字节数组中所有字节转换成程序指定的表示他的String。
     */
    public synchronized String toString() {

        return new String(buf, 0, count);
    }

    /**
     * 根据给定的编码将当前缓存字节数组中所有字节转换成程序指定的表示他的String。
     */
    public synchronized String toString(String charsetName) throws UnsupportedEncodingException {

        return new String(buf, 0, count, charsetName);
    }

    @Deprecated
    public synchronized String toString(int hibyte) {

        return new String(buf, hibyte, 0, count);
    }

    /**
     * 关闭此流、没有什么用！
     * 关闭之后方法还是能用！
     */
    public void close() throws IOException {
    }
}
