package org.nutz.ioc.meta;

import org.nutz.ioc.Iocs;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.util.NutMap;

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
    
    private boolean optional;
    
    public IocField() {}
    
    public IocField(String value) {
        if (value.contains(":")) {
            this.value = Iocs.convert(value, false);
        } else {
            IocValue tmp = new IocValue();
            tmp.setType(IocValue.TYPE_NORMAL);
            tmp.setValue(value);
            this.value = tmp;
        }
    }

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

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}
	
	public String toJson(JsonFormat jf) {
	    if (!optional)
	        return Json.toJson(this.value, jf);
	    else{
	        NutMap map = new NutMap();
	        map.put("optional", optional);
	        map.put(this.value.getType(), this.value.getValue());
	        return Json.toJson(map, jf);
	    }
	}
}
