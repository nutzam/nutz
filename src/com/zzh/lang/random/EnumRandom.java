package com.zzh.lang.random;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;

import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;

@SuppressWarnings("unchecked")
public abstract class EnumRandom<T extends Enum> implements Random<T> {

	private RecurArrayRandom<T> r;

	protected EnumRandom() {
		try {
			Class<T> type = (Class<T>) Mirror.getTypeParams(this.getClass())[0];
			Field[] fields = type.getFields();
			ArrayList list = new ArrayList(fields.length);
			for (Field f : fields) {
				if(f.getType()==type){
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

	@Override
	public T next() {
		return r.next();
	}

}
