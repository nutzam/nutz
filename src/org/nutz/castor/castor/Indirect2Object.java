package org.nutz.castor.castor;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.nutz.castor.Castor;
import org.nutz.castor.Castors;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.json.entity.JsonEntityField;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.inject.Injecting;

@SuppressWarnings({"rawtypes"})
public class Indirect2Object extends Castor<Indirect, Object> {

	@Override
	public Object cast(Indirect model, Type toType, String... args) throws FailToCastObjectException {
	    Mirror<?> me = Mirror.me(toType);
	    Object obj = me.born();
        Map map = (Map) model.getObj();
        for(Field field : me.getFields()){
            JsonEntityField jef = JsonEntityField.eval(me, field);
            Object val = map.get(jef.getName());
            if(val == null){
                continue;
            }
            
            Injecting in = me.getInjecting(field.getName());
            if(isLeaf(val)){
                Type t = Lang.getFieldType(me, field);
                in.inject(obj, Castors.me().castTo(jef.createValue(obj, val), Lang.getTypeClass(t)));
                continue;
            }
//            in.inject(obj, jef.createValue(obj, inject(val, Lang.getFieldType(me, field))));
            Object o = Castors.me().typeCast(new Indirect(val), Indirect.class, Lang.getFieldType(me, field));
            in.inject(obj, jef.createValue(obj, o));
            
        }
        return obj;
        
//      源始实现
//		return Lang.map2Object(src, Lang.getTypeClass(toType));
	}

	private static boolean isLeaf(Object obj){
        if(obj instanceof Map){
            return false;
        }
        if(obj instanceof List){
            return false;
        }
        return true;
    }
}
