package org.nutz.castor.castor;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.castor.Castor;
import org.nutz.castor.Castors;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

@SuppressWarnings("rawtypes")
public class Indirect2Map extends Castor<Indirect, Map>{

    @SuppressWarnings("unchecked")
    public Map cast(Indirect model, Type toType, String... args)
            throws FailToCastObjectException {
        Mirror<?> me = Mirror.me(toType);
        Map re = null;
        if(me.isInterface()){
            re = new HashMap();
        } else {
            re = (Map) me.born();
        }
        
        Map map = (Map) model.getObj();
        if(me.getGenericsTypes() == null){
            re.putAll(map);
            return re;
        }
        
        Type type = me.getGenericsType(1);
        for(Object key : map.keySet()){
            Object val = map.get(key);
            if(isLeaf(val)){
                re.put(key, Castors.me().castTo(val, Lang.getTypeClass(type)));
                continue;
            }
            Object obj = Castors.me().typeCast(new Indirect(val), null, type);
//            re.put(key, inject(val, type));
            re.put(key, obj);
        }
        return re;
//      return Lang.map2Object(src, Lang.getTypeClass(toType));
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
