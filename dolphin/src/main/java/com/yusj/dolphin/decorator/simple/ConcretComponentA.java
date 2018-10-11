package com.yusj.dolphin.decorator.simple;

public class ConcretComponentA extends Decorator{

    public ConcretComponentA(Component component) {
        super(component);
    }

    @Override
    public void laugh() {
        System.out.println("Oh Yeah!!!");
        super.laugh();
    }
}
