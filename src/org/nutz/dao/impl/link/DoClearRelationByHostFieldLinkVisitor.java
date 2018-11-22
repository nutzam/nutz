package org.nutz.dao.impl.link;

import org.nutz.dao.entity.LinkField;
import org.nutz.dao.impl.AbstractLinkVisitor;
import org.nutz.dao.impl.entity.field.ManyManyLinkField;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.util.Pojos;

public class DoClearRelationByHostFieldLinkVisitor extends AbstractLinkVisitor {

    public void visit(Object obj, LinkField lnk) {
        if (lnk instanceof ManyManyLinkField) {
            final ManyManyLinkField mm = (ManyManyLinkField) lnk;

            final Pojo pojo = opt.maker().makeDelete(mm.getRelationName());
            pojo.append(Pojos.Items.cndColumn(    mm.getFromColumnName(),
                                                mm.getHostField(),
                                                mm.getHostField().getValue(obj)));

            opt.add(pojo);
        }
    }

}
