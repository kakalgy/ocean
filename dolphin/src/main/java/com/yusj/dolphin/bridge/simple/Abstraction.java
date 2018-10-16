package com.yusj.dolphin.bridge.simple;

public abstract class Abstraction {
    private Implementor implementor;

    public Implementor getImplementor() {
        return implementor;
    }

    public void setImplementor(Implementor implementor) {

        this.implementor = implementor;
    }

    protected void operation() {
        implementor.operation();
    }
}
