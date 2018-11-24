package org.nutz.dao.util.cri;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.jdbc.ValueAdaptor;

/**
 * between ? and ?
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class BetweenExpression extends AbstractSqlExpression {

    private static final long serialVersionUID = 1L;

	private Object min;
	private Object max;
	
	public BetweenExpression(String name, Object min, Object max) {
		super(name);
		this.min = min;
		this.max = max;
	}

	public void joinSql(Entity<?> en, StringBuilder sb) {
		if (not)
            sb.append(" NOT ");
		sb.append(_fmtcol(en)).append(' ').append("BETWEEN").append(' ').append('?').append(" AND ").append('?');
	}

	public int joinAdaptor(Entity<?> en, ValueAdaptor[] adaptors, int off) {
		MappingField mf = _field(en);
        if (null != mf) {
            adaptors[off++] = mf.getAdaptor();
            adaptors[off++] = mf.getAdaptor();
        } else {
            adaptors[off++] = Jdbcs.getAdaptorBy(min);
            adaptors[off++] = Jdbcs.getAdaptorBy(max);
        }
        return off;
	}

	public int joinParams(Entity<?> en, Object obj, Object[] params, int off) {
		params[off++] = min;
		params[off++] = max;
        return off;
	}

	public int paramCount(Entity<?> en) {
		return 2;
	}

}
