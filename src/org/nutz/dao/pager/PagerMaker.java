package org.nutz.dao.pager;

import org.nutz.dao.DatabaseMeta;

public interface PagerMaker {

	Pager make(DatabaseMeta meta, int pageNumber, int pageSize);

}
