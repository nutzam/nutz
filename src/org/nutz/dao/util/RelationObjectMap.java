package org.nutz.dao.util;

import org.nutz.dao.impl.entity.field.ManyManyLinkField;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;

/**
 * 为多对多关联做的延迟取值Map
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@SuppressWarnings("serial")
public class RelationObjectMap extends NutMap {

    private ManyManyLinkField mm;// TODO 这个字段无法序列化

    private Object host;

    private Object linked;

    public RelationObjectMap() {
        throw Lang.noImplement();
    }

    public RelationObjectMap(ManyManyLinkField mm, Object host, Object linked) {
        this.mm = mm;
        this.host = host;
        this.linked = linked;
        this.put(mm.getFromColumnName(), mm.getHostField().getValue(host));
        this.put(mm.getToColumnName(), mm.getLinkedField().getValue(linked));
    }

    @Override
    public Object get(Object key) {
        if (mm.getFromColumnName().equals(key))
            return mm.getHostField().getValue(host);
        if (mm.getToColumnName().equals(key))
            return mm.getLinkedField().getValue(linked);
        return super.get(key);
    }

}
