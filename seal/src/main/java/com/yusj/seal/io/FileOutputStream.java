package com.yusj.seal.io;

import sun.nio.ch.FileChannelImpl;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @Description
 * @Author kakalgy
 * @Date 2018/10/10 23:04
 **/
public class FileOutputStream extends OutputStream {

    private final FileDescriptor fd;
    /*
     * 用于标识写入的内容是追加还是替换
     */
    private final boolean append;

    private FileChannel channel;

    private final String path;

    private final Object closeLock = new Object();
    private volatile boolean closed = false;

    /**
     * 根据具体文件名打开一个文件字节输出流。name中表示的文件夹必须存在、文件可以自动创建
     * *比如String name = "D:\\directory1\\directory2\\fos.txt"; 则在D盘中必须有个目录
     * *名为directory1且还有下一层目录directory2。至于是否有fos.txt没有关系、有：fos直接写入
     * *没有：fos写入之前会创建一个新的。
     * *实质是调用下面的 public FileOutputStream(File file)来创建fos;
     */
    public FileOutputStream(String name) throws FileNotFoundException {
        this(name != null ? new File(name) : null, false);
    }

    /**
     * 同上面一个构造方法、只是比上面一个构造方法多了一个参数 boolean append、
     * * 1）若append 值为  true 则表示保留原文件中数据、在其后面添加新的数据。
     * * 2）若append 值为false 如果文件存在、则清空文件内容、如果文件不存在则创建新的。
     * * 也可看出、上面一个构造方法就是默认append为false的这个构造方法的特例。
     * * 实质是调用下面的public FileOutputStream(File file, boolean append)来创建fos;
     */
    public FileOutputStream(String name, boolean append)
            throws FileNotFoundException {
        this(name != null ? new File(name) : null, append);
    }

    /**
     * 根据指定的File来创建一个到此文件的文件字节输出流。
     */
    public FileOutputStream(File file) throws FileNotFoundException {
        this(file, false);
    }

    /**
     * 根据指定的File来创建一个到此文件的文件字节输出流。可以指定是追加还是替换。
     */
    public FileOutputStream(File file, boolean append)
            throws FileNotFoundException {
        String name = (file != null ? file.getPath() : null);
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkWrite(name);
        }
        if (name == null) {
            throw new NullPointerException();
        }
        //由于内部原因无法生成代码
//        if (file.isInvalid()) {
//            throw new FileNotFoundException("Invalid file path");
//        }
        this.fd = new FileDescriptor();
        //由于内部原因无法生成代码
//        fd.attach(this);
        this.append = append;
        this.path = name;
        //调用底层方法真正的建立到指定文件的字节输出流
        open(name, append);
    }

    /**
     * 通过文件描述符打开一个到实际文件的文件字节输出流。
     */
    public FileOutputStream(FileDescriptor fdObj) {
        SecurityManager security = System.getSecurityManager();
        if (fdObj == null) {
            throw new NullPointerException();
        }
        if (security != null) {
            security.checkWrite(fdObj);
        }
        this.fd = fdObj;
        this.append = false;
        this.path = null;
//由于内部原因无法生成代码
//        fd.attach(this);
    }

    /**
     * 底层————打开一个准备写入字节的文件、模式为替换/追加。
     */
    private native void open0(String name, boolean append)
            throws FileNotFoundException;

    private void open(String name, boolean append)
            throws FileNotFoundException {
        open0(name, append);
    }

    /**
     * 底层————向文件中写入一个字节
     */
    private native void write(int b, boolean append) throws IOException;

    public void write(int b) throws IOException {
        write(b, append);
    }

    /**
     * 底层————将一个起始下标为  off 长度为len的字节数组 b 文件中
     */
    private native void writeBytes(byte b[], int off, int len, boolean append)
            throws IOException;

    /**
     * 底层————将整个字节数组 b 文件中
     */
    public void write(byte b[]) throws IOException {
        writeBytes(b, 0, b.length, append);
    }

    /**
     * 调用底层writerBytes(byte b, int off, int len)将一个起始下标为  off 长度为len的字节数组 b 文件中
     */
    public void write(byte b[], int off, int len) throws IOException {
        writeBytes(b, off, len, append);
    }

    /**
     * 关闭此流、并释放所有与此流有关的资源。同时将建立在此流基础上的FileChannel也关闭。
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
     * 获得此流对应的文件描述符。
     */
    public final FileDescriptor getFD() throws IOException {
        if (fd != null) {
            return fd;
        }
        throw new IOException();
    }

    /**
     * 获得与此流相关的FileChannel。
     */
    public FileChannel getChannel() {
        synchronized (this) {
            if (channel == null) {
                channel = FileChannelImpl.open(fd, path, false, true, append, this);
            }
            return channel;
        }
    }

    /**
     * 确保此流不再被引用时、将此流中的数据flush到指定文件中并且关闭此流。
     */
    protected void finalize() throws IOException {
        if (fd != null) {
            if (fd == FileDescriptor.out || fd == FileDescriptor.err) {
                flush();
            } else {
                /* if fd is shared, the references in FileDescriptor
                 * will ensure that finalizer is only called when
                 * safe to do so. All references using the fd have
                 * become unreachable. We can call close()
                 */
                close();
            }
        }
    }

    private native void close0() throws IOException;

    private static native void initIDs();

    static {
        initIDs();
    }
}
