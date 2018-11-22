package org.nutz.dao.util.cri;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.impl.sql.pojo.NoParamsPItem;

/**
 * 在表达式之间插入字符串
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Static extends NoParamsPItem implements SqlExpression {

    private static final long serialVersionUID = 1L;

    private String str;

    public Static(String str) {
        this.str = str;
    }

    @Override
    public SqlExpression setNot(boolean not) {
        return this;
    }

    @Override
    public String toString() {
        return ' ' + str + ' ';
    }

    @Override
    public void joinSql(Entity<?> en, StringBuilder sb) {
        sb.append(' ').append(str).append(' ');
    }

}