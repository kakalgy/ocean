package com.yusj.dolphin.bridge.simple;

import org.junit.Test;

public class SimpleTest {

    @Test
    public void testBridge(){
        Abstraction abstraction = new RefinedAbstraction();

        abstraction.setImplementor(new ConcreateImplementorA());
        abstraction.operation();

        abstraction.setImplementor(new ConcreateImplementorB());
        abstraction.operation();
    }
}
