package org.nutz.dao.util.cri;

import org.nutz.dao.sql.PItem;

public interface SqlExpression extends PItem{

    SqlExpression setNot(boolean not);

}
