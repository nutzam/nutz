/**
 * 
 */
package org.nutz.dao.util.cri;

import org.nutz.dao.entity.Entity;

/**
 * @author mei
 *
 */
public class ParamsSqlRange extends ParamsSqlExpression{

	/**
	 * @param name
	 */
	protected ParamsSqlRange(String name, String sql, Object... args) {
		super(name, sql, args);
	}

	/* (non-Javadoc)
	 * @see org.nutz.dao.sql.PItem#joinSql(org.nutz.dao.entity.Entity, java.lang.StringBuilder)
	 */
	@Override
	public void joinSql(Entity<?> en, StringBuilder sb) {
        sb.append(_fmtcol(en));
        if (not)
            sb.append(" NOT");
        sb.append(" IN (");
        sb.append(sql);
        sb.append(")");
	}

}
