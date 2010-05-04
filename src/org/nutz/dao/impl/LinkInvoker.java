package org.nutz.dao.impl;

import org.nutz.dao.entity.Link;

public interface LinkInvoker {
	void invoke(Link link, Object ta);
}
