package org.nutz.mvc.adaptor.meta;

import java.util.Map;

public class Pet {

    public String name;

    public Map<String, Pet> map;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
