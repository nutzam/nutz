package org.nutz.dao.impl.link;

import org.nutz.dao.entity.LinkField;

import org.nutz.dao.impl.AbstractLinkVisitor;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.util.Pojos;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;

public class DoDeleteLinkVisitor extends AbstractLinkVisitor {

	public void visit(Object obj, LinkField lnk) {
		Object value = lnk.getValue(obj);

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
