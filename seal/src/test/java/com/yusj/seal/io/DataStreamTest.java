package com.yusj.seal.io;

import org.junit.Test;

import java.io.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class DataStreamTest {

    private static final byte[] byteArray = {0x61, 0x62, 0x63, 0x64, 0x65};

    private static byte[] buff = new byte[5];

    private static DataOutputStream dataOutputStream;

    private static DataInputStream dataInputStream;

    @Test
    public void testDataOutputStream() throws IOException {
        dataOutputStream = new DataOutputStream(new FileOutputStream(new File("C:" + File.separator + "Users" + File.separator +
                "Public" + File.separator + "IdeaTest" + File.separator + "dos1.txt")));
        dataOutputStream.write(0x61);
        dataOutputStream.write(byteArray, 0, 3);
        dataOutputStream.write(byteArray);
        dataOutputStream.writeBoolean(false);
        dataOutputStream.writeByte(0x61);
        dataOutputStream.writeShort(32766);
        dataOutputStream.writeChar("c".charAt(0));
        dataOutputStream.writeInt(214783647);
        System.out.println(dataOutputStream.size());
        dataOutputStream.writeFloat(5.12F);//一个float类型数据占4个字节

        dataOutputStream.writeDouble(55.55);
        System.out.println(dataOutputStream.size());
        dataOutputStream.writeUTF("e");
        System.out.println(dataOutputStream.size());
        dataOutputStream.writeUTF("a陈画b");//这里的一个汉字占3个字节、外加两个表示字符串长度的字节

        System.out.println(dataOutputStream.size());
        dataOutputStream.writeLong(1L);

        System.out.println("dos 总字节数：" + dataOutputStream.size());
        dataOutputStream.close();

    }

    /**
     * DataInputstream 测试：这里要注意：怎么用DataOutputStream写入的、就要怎么读出来、
     * 即读取类型的顺序要和写入类型的顺序一致、      
     * <p>
     * 原因：因为DataoutputStream将java基本类型写入out中时、是先把基本类型转换成一定顺序的字节写入的、
     * DataInputStream读取的也是根据不同读取方法读取不同个数的字节、再转换成相应的类型返回。顺序不一致转换结构就很可能不是我们想要的。
     */

    @Test
    public void testDataInputStream() throws IOException {
        //创建以FileInputStream为基础流的dis；
        dataInputStream = new DataInputStream(new FileInputStream(new File("C:" + File.separator + "Users" + File.separator +
                "Public" + File.separator + "IdeaTest" + File.separator + "dos2.txt")));

        System.out.println(byteToString((byte) dataInputStream.read()));
        //System.out.println(dis.readUnsignedByte());与上面方法相比、这个方法是读取字节的无符号形式、即将读取的byte左侧补零返回0 - 255 之间的整数。
        System.out.println("有效字节数：" + dataInputStream.available());
        dataInputStream.readFully(buff, 0, 3);
        printByteValue(buff);

        dataInputStream.readFully(buff);
        printByteValue(buff);

        System.out.println(dataInputStream.readBoolean());
        System.out.println(byteToString(dataInputStream.readByte()));
        System.out.println(dataInputStream.readShort());

        //      System.out.println(dis.readUnsignedShort());与readUnsignedByte()相同读取的是无符号、并且将左侧补零。

        System.out.println(dataInputStream.readChar());
        System.out.println(dataInputStream.readInt());
        System.out.println(dataInputStream.readFloat());
        System.out.println(dataInputStream.readDouble());

        /**
         *这里在使用之前有个困惑：这个方法是怎么知道我要读的这个字符串是哪个？是多长？如果我连续写入两个字符串会不会一起读出来？
         *很明显、从打印结果可以看出：程序会按照顺序读取你想要读取的那一个字符串、不会多读、也不会少读、          
         *那程序是怎么识别的？也就是说程序是如何分割每个字符串的？
         *
         * 解铃还须系铃人：DataOutputStream源码中的writeUTF(String str)方法、在写入真正的String str之前会先写入两个字节、用来表示这个字符串的长度。          * 到这里就基本明白了：当DataInputSrteam的readUTF()方法开始读取字符串时、首先读到的是前两个表示这个字符串长度的字节、然后读取这个长度的字节转换成str。
         */
        System.out.println(dataInputStream.readUTF());
        System.out.println(dataInputStream.readUTF());
        System.out.println(dataInputStream.readLong());

    }

    @Test
    public void testDataInputStreamSkipByte() throws IOException {
        //使用这个方法之前要对dos写入每个类型时、这个类型在dos指定的目的地中占多少个字节要掌握。尤其是写入字符串时、会多写入两个字节来表示字符串的长度！

        dataInputStream = new DataInputStream(new FileInputStream(new File("C:" + File.separator + "Users" + File.separator +
                "Public" + File.separator + "IdeaTest" + File.separator + "dos3.txt")));
        dataInputStream.skip(1);//FileInputStream 本身的skip函数
        System.out.println(dataInputStream.available());
        dataInputStream.read(buff, 0, 3);
        printByteValue(buff);
        dataInputStream.skipBytes(5);//DataInputStream自己新增的函数
        System.out.println(dataInputStream.readBoolean());
        //这里跳的超过dos总有效字节后、会取负、ByteArrayInputStream返回的是非负数 count - pos;
        dataInputStream.skip(dataOutputStream.size() + 1);
        System.out.println(dataInputStream.available());
    }

    private static void printByteValue(byte[] buf) {
        for (byte b : buf) {
            if (b != 0) {
                System.out.print(byteToString(b) + " ");
            }
        }
        System.out.println();
    }

    private static String byteToString(byte b) {
        byte[] bAray = {b};
        return new String(bAray);
    }
}
