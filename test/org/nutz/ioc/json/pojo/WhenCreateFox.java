package org.nutz.ioc.json.pojo;

import org.nutz.ioc.IocEventTrigger;

public class WhenCreateFox implements IocEventTrigger<Animal> {

    public void trigger(Animal obj) {
        obj.setName("$" + obj.getName());
    }

}
