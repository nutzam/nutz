package org.nutz.dao.impl.link;

import org.nutz.dao.entity.LinkField;
import org.nutz.dao.impl.AbstractLinkVisitor;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.util.Pojos;

public class DoClearLinkVisitor extends AbstractLinkVisitor {

    public void visit(final Object obj, final LinkField lnk) {
        Pojo pojo = opt.maker().makeDelete(lnk.getLinkedEntity());
        pojo.append(Pojos.Items.cnd(lnk.createCondition(obj)));
        pojo.setOperatingObject(obj);
        opt.add(pojo);
    }

}
