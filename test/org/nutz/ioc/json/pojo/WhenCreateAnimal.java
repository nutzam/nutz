package org.nutz.ioc.json.pojo;

import org.nutz.ioc.IocEventTrigger;

public class WhenCreateAnimal implements IocEventTrigger<Animal> {

    public void trigger(Animal obj) {
        for (int i = 0; i < 10; i++)
            obj.onCreate();
    }

}
