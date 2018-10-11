package com.yusj.dolphin.decorator.simple;

import org.junit.Test;

public class DecoratorTest {
    @Test
    public void testDecorator() {
        Component component = new ConcretComponent();

        Component decoratorA = new ConcretComponentA(component);
        Component decoratorB = new ConcretComponentB(component);

        System.out.println("以下是A的laugh");
        decoratorA.laugh();

        System.out.println();


        System.out.println("以下是B的laugh");
        decoratorB.laugh();
    }
}
