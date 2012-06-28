package org.nutz.dao.impl.link;

import java.util.Map;

import org.nutz.dao.Condition;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.LinkField;
import org.nutz.dao.impl.AbstractLinkVisitor;
import org.nutz.dao.impl.entity.field.ManyManyLinkField;
import org.nutz.dao.sql.PItem;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.util.Pojos;

public class DoUpdateRelationLinkVisitor extends AbstractLinkVisitor {

    private Map<String, Object> map;

    private PItem[] items;

    public DoUpdateRelationLinkVisitor(Map<String, Object> map, Condition cnd) {
        this.map = map;
        this.items = Pojos.Items.cnd(cnd);
    }

    public void visit(Object obj, LinkField lnk) {
        if (lnk instanceof ManyManyLinkField) {
            ManyManyLinkField mm = (ManyManyLinkField) lnk;
            Entity<?> en = opt.makeEntity(mm.getRelationName(), map);
            Pojo pojo = opt.maker().makeUpdate(en, null);
            pojo.setOperatingObject(map);
            pojo.append(items);
            opt.add(pojo);
        }
    }

}
