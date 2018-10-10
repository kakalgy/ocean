package com.yusj.seal.io;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Description
 * @Author kakalgy
 * @Date 2018/10/10 22:41
 **/
public class FileStreamTest {

    private static final byte[] byteArray = {
            0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6A, 0x6B, 0x6C, 0x6D, 0x6E, 0x6F,
            0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7A
    };

    @Test
    public void testFileOutputStream() throws IOException {

        //创建一个模式为替换（即在写入前会将文件中的所有内容清空、如果文件存在的话）的文件字节输出流。
        FileOutputStream fosAppendFalse = new FileOutputStream(new File("D:\\fosAppendFalse.txt"));
        /**
         * 方法说明：
         * 1）写入一个字节；
         * 2）写入一个换行符；
         * 3）写入byteArray的前五个字节；
         * 4）写入换行符；
         * 5）写入整个byteArray；
         * 6）关闭流释放资源；
         * 注意：你写入文件的字节都会被底层按照系统默认编码转换成字符存到文件中、
         * 当使用文件字节输出来的时候会被反转成字节！！！自己可以动手试试！
         */
        fosAppendFalse.write(byteArray[0]);
        fosAppendFalse.write("\r\n".getBytes());
        fosAppendFalse.write(byteArray, 0, 5);
        fosAppendFalse.write("\r\n".getBytes());
        fosAppendFalse.write(byteArray);
        fosAppendFalse.close();
        /**
         * 运行结果:
         * 在D盘下生成一个名为fosAppendFalse.txt的文本文件
         * 无论运行多少次fosAppendFalse.txt只有：
         *  a
         *	abcde
         *	abcdefghijklmnopqrstuvwxyz
         *	这些内容。
         */

        //创建一个模式为追加的文件字节输出流
        FileOutputStream fosAppendTure = new FileOutputStream("D:\\fosAppendTure.txt", true);
        /**
         * 方法说明：
         * 1）向指定文件追加一个字节；
         * 2）向指定文件追加一个换行符；
         * 3）向指定文件追加byteArray的前五个字节；
         * 4）向指定文件追加换行符；
         * 5）向指定文件追加整个byteArray；
         * 6）关闭流释放资源；
         */
        fosAppendTure.write(byteArray[0]);
        fosAppendTure.write("\r\n".getBytes());
        fosAppendTure.write(byteArray, 0, 5);
        fosAppendTure.write("\r\n".getBytes());
        fosAppendTure.write(byteArray);
        fosAppendTure.write("\r\n".getBytes());
        fosAppendTure.close();
        /**
         * 运行结果:
         * 在D盘下生成一个名为fosAppendFalse.txt的文本文件
         * 每运行一次fosAppendFalse.txt中都会追加下面内容：
         *  a
         *	abcde
         *	abcdefghijklmnopqrstuvwxyz
         *	这些内容。注意换行符对结构的影响。
         */

    }

    @Test
    public void testFileInputSteram() throws IOException {
        //上面的FileOutputStream为了测试、写入文件的东西比较凌乱、下面为了方便观察结果、从新弄一个：将a-z写入文件fis.txt中、将fis.txt作为文件字节输入流的源。
        FileOutputStream fos = new FileOutputStream("D:\\fis.txt");
        fos.write(byteArray);

        //创建fis.txt的输入流
        FileInputStream fis = new FileInputStream(new File("D:\\fis.txt"));

        /**
         * 简单说明：
         * 下面写的有些乱、主要就是测试available()、read()、read(byte[] b ,int off, int len)、read(byte[] b)
         * 四个方法的使用、以及调用同一个流的后三个方法进行读取会有什么效果。
         */
        int availableCount = fis.available();
        System.out.println(availableCount);

        int n = 0;
        byte[] buf = new byte[availableCount];

        n = fis.read();
        System.out.print("read()读取的字节 : " + byteToString((byte) n));
        System.out.println("\r\n----------------------------------");

        fis.skip(5);
        availableCount = fis.available();
        System.out.println(availableCount);
        n = fis.read(buf, 0, availableCount - 1);
        System.out.println("read(byte[] b,int off, int len)读取的字节数： " + n);
        printByteValue(buf);
        System.out.println("\r\n----------------------------------");

        availableCount = fis.available();
        System.out.println(availableCount);
        byte[] buf2 = new byte[1024];
        n = fis.read(buf2);
        System.out.println("read(byte[] b)读取的字节数： " + n);
        printByteValue(buf2);
        System.out.println("\r\n----------------------------------");

        availableCount = fis.available();
        System.out.println(availableCount);
        byte[] buf3 = new byte[1024];
        n = fis.read(buf3, 0, 5);
        System.out.println("read(byte[] b,int off, int len)： " + n);
        printByteValue(buf3);
        System.out.println("\r\n----------------------------------");

        System.out.println("是否支持mark？" + fis.markSupported());
		/*System.out.println(fis.available());
		fis.reset();*/


        /**
         * 结果说明：
         * 1）result : 26  						//是实际文件D:\fis.txt中有多少有效的可读取的字节数;
         * 2）result : read()读取的字节 : a		//实际文件D:\fis.txt中第一个字节、并且标记读取位置的偏移量+1、偏移量指的是下一个被读取的字节位置;
         * 3）result : 20	//实际文件D:\fis.txt中剩余有效可读取字节数、原因1、read()读取一个字节、偏移量+1、2、fis.skip(5)使得偏移量 +5 即是从第七个开始读取。
         * 4）result : read(byte[] b,int off, int len)读取的字节数： 19		//读取的字节数取决于两个方面：1、传入的参数中的len、2、文件中剩余的有效字节数、两则取最小的那个;
         * 5）result : g h i j k l m n o p q r s t u v w x y  		//将取出的24个字节打印出来、并且标记读取位置的       偏移量  = 偏移量+实际读取字节数;
         * 6）result : read(byte[] b)读取的字节数： 1		//同结果4的说明
         * 7）result : z		//同结果5的说明
         * 8）result : 0		//实际文件中有效可读取字节为0 、即读取到文件最后了。偏移量 = available;
         * 9）result : read(byte[] b,int off, int len)： -1		//读取到文件结尾、返回-1作为标志;
         * 10） result : 是否支持mark？false 		//表示文件输入流不支持mark 所以后面的mark(10)不会有任何效果、并且如果调用父类的 reset()会报错 ：mark/reset not supported
         */
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

    @Test
    public void copyFile() throws IOException {
        //结合使用、实现复制文件、这里没有对流油任何装饰、后面再说。
        FileInputStream fis = new FileInputStream(new File("D:\\fosAppendFalse.txt"));
        FileOutputStream fosAppendFalse = new FileOutputStream(new File("D:\\CopeOfFosAppendFalse.txt"));
        byte[] b = new byte[1024];
        int n = 0;
        while ((n = fis.read(b)) != -1) {
            fosAppendFalse.write(b, 0, n);
        }
    }

//    /**
//     * 测试的时候最好一个一个方法的测试、在动手自己修改、看与自己想的结果是否一样？
//     * 多试、加深理解！
//     *
//     * @param args
//     * @throws IOException
//     */
//    public static void main(String[] args) throws IOException {
//
//        //testFileOutputStream();
//        testFileInputSteram();
//        //copyFile();
//    }
}


