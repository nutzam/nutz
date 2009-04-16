package com.zzh.dao.impl;

import com.zzh.dao.entity.Link;

abstract class LinkInvoker {
	abstract void invoke(Link link, Object ta);
}
