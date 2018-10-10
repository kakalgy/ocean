package com.yusj.seal.io;

import sun.nio.ch.FileChannelImpl;

import java.io.*;
import java.io.Closeable;
import java.nio.channels.FileChannel;

/**
 * 关于File的输入输出流中的关键实现方法都是系统方法、所以对于fis、fos的使用、知道中间的过程就行。
 *
 * @Author kakalgy
 * @Date 2018/10/10 22:20
 **/
public class FileInputStream extends InputStream {
    /**
     * 一个打开到指定文件的连接或者句柄
     **/
    private final FileDescriptor fd;


    private final String path;

    /**
     * 文件夹通道
     **/
    private FileChannel channel = null;

    private final Object closeLock = new Object();
    private volatile boolean closed = false;

    /**
     * 通过“文件路径名”创建一个对应的“文件输入流”。
     */
    public FileInputStream(String name) throws FileNotFoundException {
        this(name != null ? new File(name) : null);
    }

    /**
     * 通过“文件对象”创建一个对应的“文件输入流”。
     */
    public FileInputStream(File file) throws FileNotFoundException {
        String name = (file != null ? file.getPath() : null);
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkRead(name);
        }
        if (name == null) {
            throw new NullPointerException();
        }
        //由于内部原因无法生成代码
//        if (file.isInvalid()) {
//            throw new FileNotFoundException("Invalid file path");
//        }
        fd = new FileDescriptor();
        //由于内部原因无法生成代码
//        fd.attach(this);
        path = name;
        //打开到文件名指定的文件的连接、要对文件进行操作、当然要先获得连接
        open(name);
    }

    /**
     * 通过“文件描述符”创建一个对应的“文件输入流”。
     * 很少用！
     */
    public FileInputStream(FileDescriptor fdObj) {
        SecurityManager security = System.getSecurityManager();
        if (fdObj == null) {
            throw new NullPointerException();
        }
        if (security != null) {
            security.checkRead(fdObj);
        }
        fd = fdObj;
        path = null;

        /*
         * FileDescriptor1 is being shared by streams.
         * Register this stream with FileDescriptor1 tracker.
         */
        //由于内部原因无法生成代码
//        fd.attach(this);
    }

    /**
     * 通过文件名打开到“文件名指定的文件”的连接
     */
    private native void open0(String name) throws FileNotFoundException;

    private void open(String name) throws FileNotFoundException {
        open0(name);
    }


    @Override
    public int read() throws IOException {
        return read0();
    }

    /**
     * 底层————读取“指定文件”下一个字节并返回、若读取到结尾则返回-1。
     */
    private native int read0() throws IOException;

    /**
     * 底层————读取“指定文件”最多len个有效字节、并存放到下标从 off 开始长度为 len 的 byte[] b 中、返回读取的字节数、若读取到结尾则返回-1。
     */
    private native int readBytes(byte b[], int off, int len) throws IOException;

    /**
     * 调用底层readbytes(b, 0, b.length)读取b.length个字节放入b中、返回读取的字节数、若读取到结尾则返回-1。
     */
    public int read(byte b[]) throws IOException {
        return readBytes(b, 0, b.length);
    }

    /**
     * 调用底层readbytes(b, off, len)读取b.length个字节放入下标从 off 开始长度为 len 的 byte[] b 中、返回读取的字节数、若读取到结尾则返回-1。
     */
    public int read(byte b[], int off, int len) throws IOException {
        return readBytes(b, off, len);
    }

    /**
     * 底层————从源文件中跳过n个字节、n不能为负、返回值是实际跳过的字节数。
     * 当n >= fis.available();时返回-1;
     */
    public native long skip(long n) throws IOException;

    /**
     * 底层————查询此文件字节输入流对应的源文件中可被读取的有效字节数。
     */
    public native int available() throws IOException;

    /**
     * 关闭此流、并释放所有与此流有关的资源、
     * 并且根据此流建立的channel也会被关闭。
     */
    public void close() throws IOException {
        synchronized (closeLock) {
            if (closed) {
                return;
            }
            closed = true;
        }
        if (channel != null) {
            channel.close();
        }
//由于内部原因无法生成代码
//        fd.closeAll(new Closeable() {
//            public void close() throws IOException {
//                close0();
//            }
//        });
    }

    /**
     * 获取此流对应的文件系统中实际文件的“文件描述符”。
     */
    public final FileDescriptor getFD() throws IOException {
        if (fd != null) {
            return fd;
        }
        throw new IOException();
    }

    /**
     * 获取与此流有关的FileChannel。
     */
    public FileChannel getChannel() {
        synchronized (this) {
            if (channel == null) {
                channel = FileChannelImpl.open(fd, path, true, false, this);
            }
            return channel;
        }
    }

    private static native void initIDs();

    //关闭
    private native void close0() throws IOException;

    static {
        initIDs();
    }

    /**
     * 当此流不再被使用、关闭此流。
     */
    protected void finalize() throws IOException {
        if ((fd != null) && (fd != FileDescriptor.in)) {
            /* if fd is shared, the references in FileDescriptor1
             * will ensure that finalizer is only called when
             * safe to do so. All references using the fd have
             * become unreachable. We can call close()
             */
            close();
        }
    }
}
