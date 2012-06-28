package org.nutz.dao.impl.link;

import java.util.Map;

import org.nutz.dao.FieldFilter;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.entity.LinkField;
import org.nutz.dao.impl.AbstractLinkVisitor;
import org.nutz.lang.Lang;

public class DoUpdateLinkVisitor extends AbstractLinkVisitor {

    public void visit(Object obj, final LinkField lnk) {
        Object value = lnk.getValue(obj);
        if (Lang.length(value) == 0)
            return;
        if (value instanceof Map<?, ?>)
            value = ((Map<?, ?>) value).values();

        FieldMatcher fm = FieldFilter.get(lnk.getLinkedEntity().getType());

        // 如果需要忽略 Null 字段，则为每个 POJO 都生成一条语句
        if (null != fm && fm.isIgnoreNull()) {
            opt.addUpdateForIgnoreNull(lnk.getLinkedEntity(), value, fm);
        }
        // 否则生成一条批处理语句
        else {
            opt.addUpdate(lnk.getLinkedEntity(), value);
        }

    }

}
