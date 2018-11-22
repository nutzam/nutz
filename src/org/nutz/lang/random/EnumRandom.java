package org.nutz.lang.random;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

public abstract class EnumRandom<T extends Enum<?>> implements Random<T> {

    private RecurArrayRandom<T> r;

    @SuppressWarnings("unchecked")
    protected EnumRandom() {
        try {
            Class<T> type = (Class<T>) Mirror.getTypeParams(this.getClass())[0];
            Field[] fields = type.getFields();
            List<T> list = new ArrayList<T>(fields.length);
            for (Field f : fields) {
                if (f.getType() == type) {
                    list.add((T) f.get(null));
                }
            }
            T[] ens = (T[]) Array.newInstance(type, list.size());
            this.r = new RecurArrayRandom<T>(list.toArray(ens));
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }

    public T next() {
        return r.next();
    }

}
