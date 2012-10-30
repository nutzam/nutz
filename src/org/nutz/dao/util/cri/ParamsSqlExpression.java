/**
 * 
 */
package org.nutz.dao.util.cri;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.lang.Mirror;

/**
 * @author mei
 *
 */
public abstract class ParamsSqlExpression extends AbstractSqlExpression {
	protected String sql;
	protected Object[] args;
	/**
	 * @param name
	 */
	protected ParamsSqlExpression(String name, String sql, Object... args) {
		super(name);
		this.not = false;
		this.sql = sql;
		this.args = args;
	}

	@Override
	public void joinSql(Entity<?> en, StringBuilder sb) {
		sb.append(sql);
	}

	@Override
	public int joinAdaptor(Entity<?> en, ValueAdaptor[] adaptors, int off) {
		for (int i = 0; i < args.length; i++){
			// 自动寻找适配器设置
            if (null != args[i]) {
                ValueAdaptor vab = Jdbcs.getAdaptor(Mirror.me(args[i]));
                adaptors[off++] = vab;
            }
            // null 用 Null 适配器
            else {
            	adaptors[off++] = Jdbcs.Adaptor.asNull;
            }
		}
        return off;
	}

	@Override
	public int joinParams(Entity<?> en, Object obj, Object[] params, int off) {
		for (Object arg : args)
            params[off++] = arg;
        return off;
	}


	@Override
	public int paramCount(Entity<?> en) {
		 return args.length;
	}
	
}
