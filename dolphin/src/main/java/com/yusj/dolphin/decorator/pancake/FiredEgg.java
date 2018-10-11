package com.yusj.dolphin.decorator.pancake;

public class FiredEgg extends Condiment {
    private PanCake pancake;

    public FiredEgg(PanCake pancake) {
        this.pancake = pancake;
    }

    @Override
    public String getDesc() {
        return pancake.getDesc() + ", 煎蛋";
    }

    @Override
    public double price() {
        return pancake.price() + 2;
    }
}
