package org.nutz.dao.impl.link;

import org.nutz.dao.entity.LinkField;

import org.nutz.dao.impl.AbstractLinkVisitor;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.util.Pojos;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class DoDeleteLinkVisitor extends AbstractLinkVisitor {
    
    private static final Log log = Logs.get();

    public void visit(Object obj, LinkField lnk) {
        Object value = lnk.getValue(obj);
        if (value == null || Lang.length(value) == 0) {
            log.infof("Value of LinkField(@%s-->%s.%s) is null or isEmtry, ingore",
                       lnk.getLinkType(), lnk.getEntity().getType().getSimpleName(),
                       lnk.getHostField().getName());
            return;
        }

        final Pojo pojo = opt.maker().makeDelete(lnk.getLinkedEntity());
        pojo.setOperatingObject(value);
        pojo.append(Pojos.Items.cndAuto(lnk.getLinkedEntity(), null));
        Lang.each(value, new Each<Object>() {
            public void invoke(int i, Object ele, int length) throws ExitLoop, LoopException {
                pojo.addParamsBy(ele);
            }
        });

        opt.add(pojo);
    }

}
