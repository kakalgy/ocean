package com.yusj.dolphin.decorator.simple;

public class ConcretComponentB extends Decorator{

    public ConcretComponentB(Component component) {
        super(component);
    }

    @Override
    public void laugh() {
        System.out.println("No no no ....");
        super.laugh();
        System.out.println("This can't be true!");
    }
}
