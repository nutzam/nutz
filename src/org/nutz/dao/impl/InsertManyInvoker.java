package org.nutz.dao.impl;

import java.lang.reflect.Field;

import org.nutz.dao.Dao;
import org.nutz.dao.entity.Link;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

class InsertManyInvoker extends InsertInvoker {

	public InsertManyInvoker(Dao dao, Object mainObj, Mirror<?> mirror) {
		super(dao, mainObj, mirror);
	}

	void invoke(final Link link, Object many) {
		Object first = Lang.first(many);
		if (null != first) {
			Field refer = link.getReferField();
			if (null == refer) {
				Lang.each(many, new Each<Object>() {
					public void invoke(int index, Object ta, int size) throws ExitLoop {
						dao.insert(ta);
					}
				});
			} else {
				final Object value = mirror.getValue(mainObj, refer);
				final Mirror<?> mta = Mirror.me(first.getClass());
				Lang.each(many, new Each<Object>() {
					public void invoke(int index, Object ta, int size) throws ExitLoop {
						mta.setValue(ta, link.getTargetField(), value);
						dao.insert(ta);
					}
				});
			}
		}
	}
}
