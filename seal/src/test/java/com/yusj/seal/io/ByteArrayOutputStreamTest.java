package com.yusj.seal.io;

import org.junit.Test;


import java.io.IOException;

/**
 * @Description
 * @Author kakalgy
 * @Date 2018/10/10 22:09
 **/
public class ByteArrayOutputStreamTest {

    //对应String "abcdefghijklmnopqrstuvwxyz";
    private static final byte[] byteArray = {
            0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6A, 0x6B, 0x6C, 0x6D, 0x6E, 0x6F,
            0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7A
    };

    @Test
    public void test() throws IOException {

        //一般创建一个带有默认大小的缓存字节数组  buf 的字节输出流就可以了。
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //字节数组输出流中的 buf 中有效的字节数、缓存字节数组是存在程序内存中的。
        System.out.println(baos.size());

        /**
         * 会把int型97强转成byte类型写入 buf 中、baos.toString()是按照系统默认的编码将字节转换成字符串
         * toString()的内部实现关键代码就是：return new String(buf, 0, count);
         * result: a
         */
        baos.write(97);
        System.out.println(baos.toString());

        /**
         * 将byteArray的前四个字节添加到缓存字节数组中
         * result: aabcd
         */

        baos.write(byteArray, 0, 4);
        System.out.println(baos.toString());

        //将整个字节数组添加到缓存字节数组中去
        baos.write(byteArray);
        System.out.println(baos.toString());

        //将baos中buf的字节转入到一个新的byte[]中去。核心代码：Arrays.copyOf(buf, count);
        byte[] newByteArray = baos.toByteArray();
        for (byte b : newByteArray) {
            System.out.println(String.valueOf(b));
        }

        /**
         * baos.writeTo()方法的本质是调用传入的OutputStream实现类的write(byte[] b, 0, len);方法、
         * 将baos现有的buf字节写入到实现类指定的目的地中。
         * result: 在D盘有个名为 bytearrayoutputstream.txt的文件夹、里面有一段为“aabcdabcdefghijklmnopqrstuvwxyz”的内容。
         * 至于为什么不是字节型、这与FileOutputStream的write(byte[] b, 0, len);有关、后面讨论。
         *
         */
//        baos.writeTo(new FileOutputStream(new File("D:" + File.separator + "bytearrayoutputstream.txt")));

        /**
         * 将缓存数组清零
         *	result: 缓存字节数组清零前：31
         *	result: 缓存字节数组清零后：0
         */

        System.out.println("缓存字节数组清零前：" + baos.size());
        baos.reset();
        System.out.println("缓存字节数组清零后：" + baos.size());
    }

}


