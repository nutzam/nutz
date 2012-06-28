package org.nutz.aop.javassist.meta;

import org.nutz.lang.Lang;
import org.nutz.lang.random.R;

public class Vegetarian {

    public enum BEH {
        run, fight, lecture, fly
    }

    private String name;

    protected int run(int distance) {
        return (int) ((double) distance / (double) R.random(300, 500) * 1000);
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Object doSomething(BEH a) {
        if (a == BEH.run) {
            return run(30);
        } else if (a == BEH.fight) {
            throw new RuntimeException("No!!! Rhinoceros like peace!");
        } else if (a == BEH.lecture) {
            throw new RuntimeException("momo~~~~");
        } else if (a == BEH.fly) {
            throw new Error("The rhinoceros died, because it jump from cliff!");
        }
        throw new RuntimeException("What should the rhinoceros do next?");
    }

    void defaultMethod() {
        privateMethod();
    }

    private void privateMethod() {}

    public String[] returnArrayMethod() {
        return Lang.array("A", "B", "C");
    }
}
