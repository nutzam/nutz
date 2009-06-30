package org.nutz.dao.impl;

import org.nutz.dao.entity.Link;

abstract class LinkInvoker {
	abstract void invoke(Link link, Object ta);
}
