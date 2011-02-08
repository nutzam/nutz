package org.nutz.el.val;

import java.util.Collection;
import java.util.Iterator;

import org.nutz.el.El;
import org.nutz.el.ElValue;

public class CollectionElValue extends PojoElValue<Collection<?>> {

	public CollectionElValue(Collection<?> obj) {
		super(obj);
	}

	public ElValue getProperty(ElValue val) {
		int max = val.getInteger();
		Iterator<?> it = obj.iterator();
		for (int i = 0; i < max; i++)
			it.next();
		return El.wrap(it.next());
	}

}
