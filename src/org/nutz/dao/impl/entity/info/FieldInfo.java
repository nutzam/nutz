package org.nutz.dao.impl.entity.info;

import java.lang.reflect.Type;

import org.nutz.lang.eject.Ejecting;
import org.nutz.lang.inject.Injecting;

abstract class FieldInfo {

    public String name;

    public Type fieldType;

    public Ejecting ejecting;

    public Injecting injecting;

}
