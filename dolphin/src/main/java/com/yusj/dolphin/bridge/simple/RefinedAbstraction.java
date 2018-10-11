package com.yusj.dolphin.bridge.simple;

public class RefinedAbstraction extends Abstraction{

    @Override
    protected void operation() {
        super.getImplementor().operation();
    }
}
