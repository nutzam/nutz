package org.nutz.dao.util;

import org.nutz.dao.impl.entity.field.ManyManyLinkField;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;

/**
 * 为多对多关联做的延迟取值Map
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class RelationObjectMap extends NutMap {

	private static final long serialVersionUID = 5310528028330500782L;

	private ManyManyLinkField mm;

	private Object host;

	private Object linked;

	public RelationObjectMap() {
		throw Lang.noImplement();
	}

	public RelationObjectMap(ManyManyLinkField mm, Object host, Object linked) {
		this.mm = mm;
		this.host = host;
		this.linked = linked;
		this.put(mm.getFromColumnName(), host);
		this.put(mm.getToColumnName(), linked);
	}

	@Override
	public Object get(Object key) {
		Object re = super.get(key);
		if (re == host)
			return mm.getHostField().getValue(host);
		if (re == linked)
			return mm.getLinkedField().getValue(linked);
		return re;
	}

}
