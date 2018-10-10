package com.yusj.seal.io;

import java.io.IOException;

/**
 * @Description
 * @Author kakalgy
 * @Date 2018/10/10 23:20
 **/
public class FilterOutputStream extends OutputStream {
    /**
     * 与FileterInputStream 的区别就是不再是volatile
     */
    protected OutputStream out;

    //通过传入的OutputStream实现类构造FilterOutputStream
    public FilterOutputStream(OutputStream out) {
        this.out = out;
    }

    public void write(int b) throws IOException {
        out.write(b);
    }

    public void write(byte b[]) throws IOException {
        write(b, 0, b.length);
    }

    public void write(byte b[], int off, int len) throws IOException {
        if ((off | len | (b.length - (len + off)) | (off + len)) < 0)
            throw new IndexOutOfBoundsException();

        for (int i = 0; i < len; i++) {
            write(b[off + i]);
        }
    }

    public void flush() throws IOException {
        out.flush();
    }

    @SuppressWarnings("try")
    public void close() throws IOException {
        //由于内部原因无法生成代码
//        try (OutputStream ostream = out) {
//            flush();
//        }
    }
}
