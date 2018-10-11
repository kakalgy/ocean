package com.yusj.seal.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.io.UTFDataFormatException;

/**
 * DataInputStrem 数据字节输入流、我们已经知道、FilterInputStream是一个装饰器、
 * * 他的子类就是为传入的InputStream子类添加新的功能、DataInputStrem作为FilterInputStream
 * * 的一个实现类就是为传入的InputStream子类提供：
 * * 应用程序将java的原始数据以一种与机器无关的方式读入、然后可以使用对应的DataOutputStream写入传入InputStream实现类自定的目的地中。
 *
 * @Author kakalgy
 * @Date 2018/10/10 23:57
 **/
public class DataInputStream extends FilterInputStream implements DataInput {
    /**
     * 调用父类FilterInputStream构造方法、在传入的InputStream实现类基础上创建DataInputStream;
     */
    public DataInputStream(InputStream in) {
        super(in);
    }

    /**
     * readUTF方法使用的初始化数组
     */
    private byte bytearr[] = new byte[80];
    private char chararr[] = new char[80];

    /**
     * 调用传入的InputStream实现类（以下简称：in）的read(byte b ,int off, int len)方法读取字节数组;
     * 装饰器设计模式的本质：在保留组件原有功能的同时为现有被修饰的组件添加新功能;
     * 至于这里为什么不调用：in.read(byte[] b)、很简单、in.read(byte[] b)本身就是调用in.read(byte[]b ,int off, int len)来实现的、何必还要走弯路;
     */
    public final int read(byte b[]) throws IOException {
        return in.read(b, 0, b.length);
    }

    /**
     * 调用in.read(byte[]b ,int off, int len)读取len个字节;
     */
    public final int read(byte b[], int off, int len) throws IOException {
        return in.read(b, off, len);
    }

    /**
     * 调用自己的 readFully(byte[] b,int off, int len)读取b.length个字节;为DataInput中定义的方法
     */
    public final void readFully(byte b[]) throws IOException {
        readFully(b, 0, b.length);
    }

    /**
     * 从in中读取len个字节、为DataInput中定义的方法
     */
    public final void readFully(byte b[], int off, int len) throws IOException {
        if (len < 0)
            throw new IndexOutOfBoundsException();
        int n = 0;
        while (n < len) {
            int count = in.read(b, off + n, len - n);
            if (count < 0)
                throw new EOFException();
            n += count;
        }
    }

    /**
     * 跳过in的前n个字节、并在以后的读取中不再读取这些字节。
     */
    public final int skipBytes(int n) throws IOException {
        int total = 0;
        int cur = 0;

        while ((total < n) && ((cur = (int) in.skip(n - total)) > 0)) {
            total += cur;
        }

        return total;
    }

    /**
     * 用于读取DataOut接口writeBoolean方法写入的字节。
     */
    public final boolean readBoolean() throws IOException {
        int ch = in.read();
        if (ch < 0)
            throw new EOFException();
        return (ch != 0);
    }

    /**
     * 用于读取DataOut接口writeByte()方法写入的字节、该字节被看作是 -128 到 127（包含）范围内的一个有符号值。
     * 即返回的仍是一个byte、而非转换后的无符号的int、从结果的返回处理也能看出来。
     */
    public final byte readByte() throws IOException {
        int ch = in.read();
        if (ch < 0)
            throw new EOFException();
        return (byte) (ch);
    }

    /**
     * 读取一个输入字节，将它左侧补零 (zero-extend) 转变为 int 类型，并返回结果，所以结果的范围是 0 到 255。
     * 如果接口 DataOutput 的 writeByte 方法的参数是 0 到 255 之间的值，则此方法适用于读取用 writeByte 写入的字节。
     */
    public final int readUnsignedByte() throws IOException {
        int ch = in.read();
        if (ch < 0)
            throw new EOFException();
        return ch;
    }

    /**
     * 读取两个输入字节并返回一个 short 值。
     */
    public final short readShort() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (short) ((ch1 << 8) + (ch2 << 0));
    }

    /**
     * 读取两个输入字节，并返回 0 到 65535 范围内的一个 int 值。
     * 如果接口 DataOutput 的 writeShort 方法的参数是 0 到 65535 范围内的值，则此方法适用于读取用 writeShort 写入的字节。
     */
    public final int readUnsignedShort() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (ch1 << 8) + (ch2 << 0);
    }

    /**
     * 读取两个输入字节并返回一个 char 值。
     */
    public final char readChar() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (char) ((ch1 << 8) + (ch2 << 0));
    }

    /**
     * 读取四个输入字节并返回一个int 值
     */
    public final int readInt() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    private byte readBuffer[] = new byte[8];

    /**
     * 读取8个输入字节并返回一个long 值
     */
    public final long readLong() throws IOException {
        readFully(readBuffer, 0, 8);
        return (((long) readBuffer[0] << 56) +
                ((long) (readBuffer[1] & 255) << 48) +
                ((long) (readBuffer[2] & 255) << 40) +
                ((long) (readBuffer[3] & 255) << 32) +
                ((long) (readBuffer[4] & 255) << 24) +
                ((readBuffer[5] & 255) << 16) +
                ((readBuffer[6] & 255) << 8) +
                ((readBuffer[7] & 255) << 0));
    }

    /**
     * 读取四个字节并返回一个float 值
     */
    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    /**
     * 读取8个字节并返回一个double值
     */
    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    private char lineBuffer[];

    /**
     * 此方法已废弃、不再过多关注
     */
    @Deprecated
    public final String readLine() throws IOException {
        char buf[] = lineBuffer;

        if (buf == null) {
            buf = lineBuffer = new char[128];
        }

        int room = buf.length;
        int offset = 0;
        int c;

        loop:
        while (true) {
            switch (c = in.read()) {
                case -1:
                case '\n':
                    break loop;

                case '\r':
                    int c2 = in.read();
                    //由于内部原因无法生成代码
//                    if ((c2 != '\n') && (c2 != -1)) {
//                        if (!(in instanceof PushbackInputStream)) {
//                            this.in = new PushbackInputStream(in);
//                        }
//                        ((PushbackInputStream) in).unread(c2);
//                    }
                    break loop;

                default:
                    if (--room < 0) {
                        buf = new char[offset + 128];
                        room = buf.length - offset - 1;
                        System.arraycopy(lineBuffer, 0, buf, 0, offset);
                        lineBuffer = buf;
                    }
                    buf[offset++] = (char) c;
                    break;
            }
        }
        if ((c == -1) && (offset == 0)) {
            return null;
        }
        return String.copyValueOf(buf, 0, offset);
    }

    /**
     * 调用下方的readUTF(DataInut in)
     */
    public final String readUTF() throws IOException {
        return readUTF(this);
    }

    /**
     * 读取输入流中的一串有序字节字节、并转换成String返回
     */
    public final static String readUTF(DataInput in) throws IOException {
        // 从“数据输入流”中读取“无符号的short类型”的值：
        // 从DataOutputStream 的 writeUTF(String str)方法知道此方法读取的前2个字节是数据的长度
        int utflen = in.readUnsignedShort();
        byte[] bytearr = null;
        char[] chararr = null;
        if (in instanceof DataInputStream) {
            DataInputStream dis = (DataInputStream) in;
            if (dis.bytearr.length < utflen) {
                dis.bytearr = new byte[utflen * 2];
                dis.chararr = new char[utflen * 2];
            }
            chararr = dis.chararr;
            bytearr = dis.bytearr;
        } else {
            bytearr = new byte[utflen];
            chararr = new char[utflen];
        }

        int c, char2, char3;
        int count = 0;
        int chararr_count = 0;

        in.readFully(bytearr, 0, utflen);

        while (count < utflen) {
            c = (int) bytearr[count] & 0xff;
            if (c > 127) break;
            count++;
            chararr[chararr_count++] = (char) c;
        }

        while (count < utflen) {
            c = (int) bytearr[count] & 0xff;
            switch (c >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    /* 0xxxxxxx*/
                    count++;
                    chararr[chararr_count++] = (char) c;
                    break;
                case 12:
                case 13:
                    /* 110x xxxx   10xx xxxx*/
                    count += 2;
                    if (count > utflen)
                        throw new UTFDataFormatException(
                                "malformed input: partial character at end");
                    char2 = (int) bytearr[count - 1];
                    if ((char2 & 0xC0) != 0x80)
                        throw new UTFDataFormatException(
                                "malformed input around byte " + count);
                    chararr[chararr_count++] = (char) (((c & 0x1F) << 6) |
                            (char2 & 0x3F));
                    break;
                case 14:
                    /* 1110 xxxx  10xx xxxx  10xx xxxx */
                    count += 3;
                    if (count > utflen)
                        throw new UTFDataFormatException(
                                "malformed input: partial character at end");
                    char2 = (int) bytearr[count - 2];
                    char3 = (int) bytearr[count - 1];
                    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
                        throw new UTFDataFormatException(
                                "malformed input around byte " + (count - 1));
                    chararr[chararr_count++] = (char) (((c & 0x0F) << 12) |
                            ((char2 & 0x3F) << 6) |
                            ((char3 & 0x3F) << 0));
                    break;
                default:
                    /* 10xx xxxx,  1111 xxxx */
                    throw new UTFDataFormatException(
                            "malformed input around byte " + count);
            }
        }
        // The number of chars produced may be less than utflen
        return new String(chararr, 0, chararr_count);
    }
}
