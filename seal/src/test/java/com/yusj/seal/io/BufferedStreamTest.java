package com.yusj.seal.io;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class BufferedStreamTest {

    private static final byte[] byteArray = {
            0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6A, 0x6B, 0x6C, 0x6D, 0x6E, 0x6F,
            0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7A
    };

    @Test
    public void testBufferedOutputStream() throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new java.io.FileOutputStream(new File("D:\\bos.txt")));
        bos.write(byteArray[0]);
        bos.write(byteArray, 1, byteArray.length - 1);
        //bos.write(byteArray); 注意：这个方法不是BufferedOutputStream的方法、此方法是调用FilterOutputStream的write(byte[] b)
        //write(byte[] b)调用本身的write(byte[]b , 0, b.length)方法、而此方法的本质是循环调用传入的out的out.write(byte b)方法.
        bos.flush();
    }

    @Test
    public void testBufferedInpuStream() throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new java.io.FileInputStream(new File("D:\\bos.txt")));
        for (int i = 0; i < 10; i++) {
            if (bis.available() >= 0) {
                System.out.println(byteToString((byte) bis.read()));
            }
        }
        if (!bis.markSupported()) {
            return;
        }

        bis.mark(6666);//标记"当前索引位子" 即第11个字节 k

        bis.skip(10);//丢弃10个字节。

        //读取剩下的b.length个字节
        byte[] b = new byte[1024];
        int n1 = bis.read(b, 0, b.length);
        System.out.println("剩余有效字节 : " + n1);
        printByteValue(b);

        bis.reset();//重置输入流最后一次调用mark()标记的索引位置
        int n2 = bis.read(b, 0, b.length);
        System.out.println("剩余有效字节 : " + n2);
        printByteValue(b);
    }

    private static void printByteValue(byte[] buf) {
        for (byte b : buf) {
            if (b != 0) {
                System.out.print(byteToString(b) + " ");
            }
        }
    }

    private static String byteToString(byte b) {
        byte[] bAray = {b};
        return new String(bAray);
    }

//    public static void main(String[] args) throws IOException {
//        testBufferedOutputStream();
//        testBufferedInpuStream();
//    }
}


