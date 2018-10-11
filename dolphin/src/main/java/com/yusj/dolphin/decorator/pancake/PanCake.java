package com.yusj.dolphin.decorator.pancake;

public abstract class PanCake {
    String desc;

    public PanCake() {
        this.desc = "我是一个单纯的煎饼";
    }

    public String getDesc() {
        return this.desc;
    }

    public abstract double price();
}
