package org.nutz.castor.castor;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nutz.castor.Castor;
import org.nutz.castor.Castors;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

@SuppressWarnings("rawtypes")
public class Indirect2List extends Castor<Indirect, List>{

    @SuppressWarnings("unchecked")
    public List cast(Indirect model, Type toType, String... args)
            throws FailToCastObjectException {
        Mirror<?> me = Mirror.me(toType);
        List re = null;
        if(me.isInterface()){
            re = new ArrayList();
        } else {
            re = (List) me.born();
        }
        List list = (List)model.getObj();
        if(me.getGenericsTypes() == null){
            return list;
        }
        Type type = me.getGenericsType(0);
        for(Object obj : list){
            if(isLeaf(obj)){
                re.add(Castors.me().castTo(obj, Lang.getTypeClass(type)));
                continue;
            }
//            re.add(inject(obj, type));
            Object o = Castors.me().typeCast(new Indirect(obj), Indirect.class, type);
            re.add(o);
        }
        return re;
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
