package com.yusj.dolphin.decorator.pancake;

public class MeatFloss extends Condiment {
    private PanCake pancake;

    public MeatFloss(PanCake pancake) {
        this.pancake = pancake;
    }

    @Override
    public String getDesc() {
        return pancake.getDesc() + ", 火腿片";
    }

    @Override
    public double price() {
        return pancake.price() + 1.5;
    }
}
