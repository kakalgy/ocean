package com.yusj.seal.io;

import java.io.IOException;
import java.io.UTFDataFormatException;

/**
 * DataOutput的实现类：将java基础类型转换成字节、写入到一个二进制流中的方法。
 * 同时还提供了一个将 String 转换成 UTF-8 修改版格式并写入所得到的系列字节的工具。
 */
public class DataOutputStream extends FilterOutputStream implements DataOutput {
    /**
     * 到目前为止写入数据输出流的字节数、实际上就是个计数器、用于size()的返回值。
     */
    protected int written;
    /**
     * writeUTF使用的变量。
     */
    private byte[] bytearr = null;

    /**
     * 通过OutputStream实现类创建一个DataOutputStream dos 、此dos继承FilterOutputStream装饰类、DataOut接口。
     * 写入字节数标记：written = 0 。
     */
    public DataOutputStream(OutputStream out) {
        super(out);
    }

    /**
     * 增加written的值、直到int类型能达到的最大的值2的31次方-1。
     */
    private void incCount(int value) {
        int temp = written + value;
        if (temp < 0) {
            temp = Integer.MAX_VALUE;
        }
        written = temp;
    }

    /**
     * 调用传入的OutputStream（以下简称out）的out.write(b)方法将int b写入out指向的目的地中。
     * 并且written + 1。
     */
    public synchronized void write(int b) throws IOException {
        out.write(b);
        incCount(1);
    }

    /**
     * 调用out.write(byte[] b, int off, int len)将b的起始下标为off长度为len字节写入out中。
     * 注意：这个方法的修饰符是synchronized、也就是这个方法完成之前不允许此类的别的方法执s行、
     * 这样做的目的就是保证写入out中的字节的有序性、这样在我们取出来的时候才不会发生各种各样的问题。
     * 同样、写完之后written + len。
     */
    public synchronized void write(byte b[], int off, int len)
            throws IOException {
        out.write(b, off, len);
        incCount(len);
    }

    /**
     * 将out中的数据flush到out指定的目的地中去。
     */
    public void flush() throws IOException {
        out.flush();
    }

    /**
     * 将一boolean型数据写入out中、written + 1。
     */
    public final void writeBoolean(boolean v) throws IOException {
        out.write(v ? 1 : 0);
        incCount(1);
    }

    /**
     * 将一个字节写入out中、written + 1。
     */
    public final void writeByte(int v) throws IOException {
        out.write(v);
        incCount(1);
    }

    /**
     * 将一个short写入out、written + 2。
     */
    public final void writeShort(int v) throws IOException {
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 0) & 0xFF);
        incCount(2);
    }

    /**
     * 将一个char写入out、written + 2。
     */
    public final void writeChar(int v) throws IOException {
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 0) & 0xFF);
        incCount(2);
    }

    /**
     * 将一个int写入out、written + 4。
     */
    public final void writeInt(int v) throws IOException {
        out.write((v >>> 24) & 0xFF);
        out.write((v >>> 16) & 0xFF);
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 0) & 0xFF);
        incCount(4);
    }

    private byte writeBuffer[] = new byte[8];

    /**
     * 将一个long写入out、written + 8。
     */
    public final void writeLong(long v) throws IOException {
        writeBuffer[0] = (byte) (v >>> 56);
        writeBuffer[1] = (byte) (v >>> 48);
        writeBuffer[2] = (byte) (v >>> 40);
        writeBuffer[3] = (byte) (v >>> 32);
        writeBuffer[4] = (byte) (v >>> 24);
        writeBuffer[5] = (byte) (v >>> 16);
        writeBuffer[6] = (byte) (v >>> 8);
        writeBuffer[7] = (byte) (v >>> 0);
        out.write(writeBuffer, 0, 8);
        incCount(8);
    }

    /**
     * 将一个float写入out。
     */
    public final void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }

    /**
     * 将一个double写入out。
     */
    public final void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }

    /**
     * 将一个字符串转换成一串有序字节写入out
     * written + s.length()；
     */
    public final void writeBytes(String s) throws IOException {
        int len = s.length();
        for (int i = 0; i < len; i++) {
            out.write((byte) s.charAt(i));
        }
        incCount(len);
    }

    /**
     * 将一个字符串以一串有序字符写入out中
     * written + s.lengt()*2;
     */
    public final void writeChars(String s) throws IOException {
        int len = s.length();
        for (int i = 0; i < len; i++) {
            int v = s.charAt(i);
            out.write((v >>> 8) & 0xFF);
            out.write((v >>> 0) & 0xFF);
        }
        incCount(len * 2);
    }

    /**
     * 调用下方 writeUTF(String str, DataOutput out) 将一个字符串写入out。
     */
    public final void writeUTF(String str) throws IOException {
        writeUTF(str, this);
    }

    /**
     * 将一个字符串写入out。
     */
    static int writeUTF(String str, DataOutput out) throws IOException {
        int strlen = str.length();
        int utflen = 0;
        int c, count = 0;

        /* use charAt instead of copying String to char array */
        // 由于UTF-8是1～4个字节不等；
        // 这里是根据UTF-8首字节的范围，判断UTF-8是几个字节的。
        for (int i = 0; i < strlen; i++) {
            c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                utflen++;
            } else if (c > 0x07FF) {
                utflen += 3;
            } else {
                utflen += 2;
            }
        }

        if (utflen > 65535)
            throw new UTFDataFormatException(
                    "encoded string too long: " + utflen + " bytes");

        byte[] bytearr = null;
        if (out instanceof DataOutputStream) {
            DataOutputStream dos = (DataOutputStream) out;
            if (dos.bytearr == null || (dos.bytearr.length < (utflen + 2)))
                dos.bytearr = new byte[(utflen * 2) + 2];
            bytearr = dos.bytearr;
        } else {
            bytearr = new byte[utflen + 2];
        }
        //字节数组前两个写入的是表示UTF-8字符串的长度、所以在写入字符串的时候会将str.length + 2 个字节写入out中。可以用写入str前后dos.size()验证一下。
        bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
        bytearr[count++] = (byte) ((utflen >>> 0) & 0xFF);

        int i = 0;
        for (i = 0; i < strlen; i++) {
            c = str.charAt(i);
            if (!((c >= 0x0001) && (c <= 0x007F))) break;
            bytearr[count++] = (byte) c;
        }

        for (; i < strlen; i++) {
            c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                bytearr[count++] = (byte) c;

            } else if (c > 0x07FF) {
                bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                bytearr[count++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            } else {
                bytearr[count++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
                bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            }
        }
        out.write(bytearr, 0, utflen + 2);
        return utflen + 2;
    }

    /**
     * 返回当前out中已经写入的字节数、如果超过int范围则返回int最大值
     */
    public final int size() {
        return written;
    }
}
