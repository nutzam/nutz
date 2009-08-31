package org.nutz.lang.random;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

@SuppressWarnings("unchecked")
public abstract class EnumRandom<T extends Enum> implements Random<T> {

	private RecurArrayRandom<T> r;

	protected EnumRandom() {
		try {
			Class<T> type = (Class<T>) Mirror.getTypeParams(this.getClass())[0];
			Field[] fields = type.getFields();
			ArrayList list = new ArrayList(fields.length);
			for (Field f : fields) {
				if (f.getType() == type) {
					list.add(f.get(null));
				}
			}
			T[] ens = (T[]) Array.newInstance(type, list.size());
			list.toArray(ens);
			this.r = new RecurArrayRandom<T>(ens);
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

	public T next() {
		return r.next();
	}

}
