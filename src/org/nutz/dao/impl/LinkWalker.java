package org.nutz.dao.impl;

import org.nutz.dao.entity.Link;

abstract class LinkWalker {
	abstract void walk(Link link);
}