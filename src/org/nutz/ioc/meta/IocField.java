package org.nutz.ioc.meta;

import org.nutz.json.Json;

/**
 * 描述了一个对象的字段，两个属性分别表示字段名，和字段值
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.ioc.meta.IocValue
 */
public class IocField {

    private String name;

    private IocValue value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IocValue getValue() {
        return value;
    }

    public void setValue(IocValue value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("{%s:%s}", name, Json.toJson(value));
    }
}
