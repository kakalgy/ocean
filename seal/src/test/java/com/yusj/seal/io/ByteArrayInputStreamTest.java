package com.yusj.seal.io;

import com.yusj.seal.io.ByteArrayInputStream;
import org.junit.Before;
import org.junit.Test;

public class ByteArrayInputStreamTest {


    private static byte[] byteArray = new byte[100];

//    static {
//        for (byte i = 0; i < 100; i++) {
//            byteArray[i] = i;
//        }
//    }

    @Before
    public void beforeTest() {
        for (byte i = 0; i < 100; i++) {
            byteArray[i] = i;
        }
    }


    @Test
    public void test() {
        //创建一个有字节缓存数组的bais、byteArray通过bais的构造方法传递给了ByteArrayInputStream缓存数组、所以此流的内容就是byteArray。

        ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
        //ByteArrayInputStream bais = new ByteArrayInputStream(byteArray, 0, byteArray.length);与上面效果相同。

        //查看此流缓存字节数组中即byteArray中有效可供读取的字节数、 result: 100
        int available = bais.available();
        System.out.println("befor skip: " + available);
        System.out.println("----------------------------------");

        //跳过此流的换成字节数组前33个即从33开始读取、因为byte是从0开始的。
        bais.skip(33);

        //查看查看此流缓存字节数组有效可供读取的字节数 result: 67
        available = bais.available();
        System.out.println("after skip: " + bais.available());
        System.out.println("----------------------------------");

        //当有效可供读取的字节数大于33个时、读取下一个、并且将读取之后、当前缓存字节数组中有效字节数赋给上面的available
        //result: 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 57 58 59 60 61 62 63 64 65 66
        while (33 < available) {
            System.out.print(bais.read() + " ");
            available = bais.available();
        }
        System.out.println("\r\n" + "----------------------------------");

        //如果ByteArrayInputStream不支持mark则退出、实际上我们从源码中已经知道此流的markSupported()一直返回的都是true
        if (!bais.markSupported()) {
            return;
        }

        //此时缓存字节数组中还剩67--99这些字节未读取。共33个。
        //result: 67 68 69 70 71 72 73 74 75 76 77 78 79 80 81 82 83 84 85 86 87 88 89 90 91 92 93 94 95 96 97 98 99
        //markB中传入的初始大小值bais.available() 是 33
        bais.mark(98);
        byte[] markB = new byte[bais.available()];
        while ((bais.read(markB, 0, markB.length)) != -1) {
            for (byte b : markB) {
                System.out.print(b + " ");
            }
        }
        System.out.println("\r\n" + "----------------------------------");

        //重置标记位置
        //result:67 68 69 70 71 72 73 74 75 76 77 78 79 80 81 82 83 84 85 86 87 88 89 90 91 92 93 94 95 96 97 98 99
        //resetB中传入的初始大小值bais.available() 是 33
        bais.reset();

        byte[] resetB = new byte[bais.available()];
        while ((bais.read(resetB, 0, resetB.length)) != -1) {
            for (byte b : resetB) {
                System.out.print(b + " ");
            }
        }
        System.out.println("\r\n" + "----------------------------------");
    }

    /**
     * 结果说明:
     * 1、前面的都好理解、本质就是每读一个字节、ByteArrayInputStream的计数器就会加一、若有不理解就看下源码。
     * 2、read() 是将从缓存字节数组中读到的字节以整数形式返回。
     * 3、read(byte[] b, int off, int len) 是将缓存字节数组中最多len个字节读取出来放到b的off下标到off+len-1中去
     * 所以我们对b的操作也就是对读取结果的操作。
     * 3、mark函数的参数98是没有任何意义的、无论你传什么整数都一样。当你调用mark函数时、此流就会把当前缓存字节数组中下一个
     * 即将被读取的字节的下标赋给mark、上面程序中就是 67、对应的字节值也是67。下面继续读取剩下的缓存字节数组中的字节值、打
     * 印的就是从67 - 99
     * 3、后面bais.reset()函数 、如果不调用这个函数直接循环打印、则没有任何输出结果、因为记录字节缓存数组的下标的值pos已经到字
     * 节数组最后了、下一个已经没有了、但是当你调用 了basi.reset()函数之后、就会输出67 - 99、原因就是当调用reset函数时、此
     * 流会把上面调用mark函数时mark所记录的当时的缓存字节数组下标重新赋给pos、所以输出结果依然是67 - 99 而非什么都没有。
     */

}


