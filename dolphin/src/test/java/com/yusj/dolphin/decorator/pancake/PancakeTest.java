package com.yusj.dolphin.decorator.pancake;

import org.junit.Test;

public class PancakeTest {

    @Test
    public void test() {
        PanCake tornCake = new TornCake();
        //手抓饼基础价
        System.out.println(String.format("%s ￥%s", tornCake.getDesc(), tornCake.price()));

        PanCake roujiamo = new Hamburger();
        roujiamo = new FiredEgg(roujiamo);
        roujiamo = new FiredEgg(roujiamo);
        roujiamo = new MeatFloss(roujiamo);
//        roujiamo = new MeatFloss(roujiamo);
//        roujiamo = new Cucumber(roujiamo);
        //我好饿
        System.out.println(String.format("%s ￥%s", roujiamo.getDesc(), roujiamo.price()));
    }
}
