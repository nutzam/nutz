package org.nutz.aop.javassist.meta;

import java.lang.reflect.Modifier;

import org.nutz.lang.Strings;

public class Vegetarians {

    public static int run(Vegetarian r, int distance) {
        return r.run(distance);
    }

    public static void main(String[] args) {
        System.out.println(Strings.fillBinary(Modifier.ABSTRACT, 32));
    }
}
