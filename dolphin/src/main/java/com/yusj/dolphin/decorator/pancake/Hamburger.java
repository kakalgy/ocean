package com.yusj.dolphin.decorator.pancake;

public class Hamburger extends PanCake{

    public Hamburger() {
        this.desc = "我是一个汉堡包";
    }

    @Override
    public double price() {
        return 6;
    }
}
