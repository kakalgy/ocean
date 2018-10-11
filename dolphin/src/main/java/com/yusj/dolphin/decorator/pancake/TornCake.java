package com.yusj.dolphin.decorator.pancake;

public class TornCake extends PanCake {

    public TornCake() {
        this.desc = "我是手抓饼";
    }

    @Override
    public double price() {
        return 4;
    }
}
