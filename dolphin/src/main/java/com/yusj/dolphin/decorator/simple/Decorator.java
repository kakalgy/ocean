package com.yusj.dolphin.decorator.simple;

public class Decorator implements Component{

    private Component component;

    public Decorator(Component component) {
        this.component = component;
    }

    public void laugh() {
        this.component.laugh();
    }
}
