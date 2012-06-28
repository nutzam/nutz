package org.nutz.dao.impl.sql.pojo;

import org.nutz.dao.Condition;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.util.Pojos;

public class ConditionPItem extends NoParamsPItem {

    private Condition cnd;

    public ConditionPItem(Condition cnd) {
        this.cnd = cnd;
    }

    public void joinSql(Entity<?> en, StringBuilder sb) {
        if (null != cnd) {
            sb.append(' ').append(Pojos.formatCondition(en, cnd));
        }
    }

}
