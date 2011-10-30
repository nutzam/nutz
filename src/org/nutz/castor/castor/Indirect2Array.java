package org.nutz.castor.castor;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nutz.castor.Castor;
import org.nutz.castor.Castors;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Mirror;

@SuppressWarnings("rawtypes")
public class Indirect2Array  extends Castor<Indirect, Object[]>{

    @SuppressWarnings("unchecked")
    public Object[] cast(Indirect list, Type toType, String... args)
            throws FailToCastObjectException {
        Mirror<?> me = Mirror.me(toType);
        Class<?> clazz = me.getType().getComponentType();
//        List list = (List) model;
        List vals = new ArrayList();
        for(Object obj : (List)list.getObj()){
            if(isLeaf(obj)){
                vals.add(Castors.me().castTo(obj, clazz));
                continue;
            }
            Object o = Castors.me().typeCast(new Indirect(obj), Indirect.class, clazz);
//            vals.add(inject(obj, clazz));
            vals.add(o);
        }
        Object obj = Array.newInstance(clazz, vals.size());
        for(int i = 0; i < vals.size(); i++){
            Array.set(obj, i, vals.get(i));
        }
        return (Object[]) obj;
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
