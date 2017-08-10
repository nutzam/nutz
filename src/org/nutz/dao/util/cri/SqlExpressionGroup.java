package org.nutz.dao.util.cri;

import static org.nutz.dao.util.cri.Exps.*;

import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.impl.sql.pojo.AbstractPItem;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.sql.Pojo;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 组合一组表达式，只能增加，不能减少
 */
public class SqlExpressionGroup extends AbstractPItem implements SqlExpression {

    private static final long serialVersionUID = 1L;

    private List<SqlExpression> exps;
    
    protected boolean not;
    
    private static final Log log = Logs.get();

    public SqlExpressionGroup() {
        exps = new ArrayList<SqlExpression>(); // 默认就是10个，能放5个条件，够了吧
    }

    public SqlExpressionGroup and(String name, String op, Object value) {
        return and(Exps.create(name, op, value));
    }

    public SqlExpressionGroup and(SqlExpression exp) {
    	if (exp == null) {
    		if (log.isTraceEnabled())
    			log.trace("ignore null SqlExpression");
    		return this;
    	}
        if (!exps.isEmpty())
            _add(new Static("AND"));
        return _add(exp);
    }

    public SqlExpressionGroup andEquals(String name, Object val) {
        if (null == val)
            return andIsNull(name);
        return and(eq(name, val));
    }

    public SqlExpressionGroup andNotEquals(String name, Object val) {
        if (null == val)
            return andNotIsNull(name);
        return and(eq(name, val).not());

    }

    public SqlExpressionGroup andIsNull(String name) {
        return and(isNull(name));
    }

    public SqlExpressionGroup andNotIsNull(String name) {
        return and(isNull(name).not());
    }

    public SqlExpressionGroup andGT(String name, long val) {
        return and(gt(name, val));
    }

    public SqlExpressionGroup andGTE(String name, long val) {
        return and(gte(name, val));
    }

    public SqlExpressionGroup andLT(String name, long val) {
        return and(lt(name, val));
    }

    public SqlExpressionGroup andLTE(String name, long val) {
        return and(lte(name, val));
    }

    public SqlExpressionGroup andIn(String name, long... ids) {
        return and(inLong(name, ids));
    }

    public SqlExpressionGroup andInIntArray(String name, int... ids) {
        return and(inInt(name, ids));
    }

    public SqlExpressionGroup andIn(String name, String... names) {
        return and(inStr(name, names));
    }

    public SqlExpressionGroup andInBySql(String name, String subSql, Object... args) {
        return and(inSql(name, subSql, args));
    }

    public SqlExpressionGroup andNotInBySql(String name, String subSql, Object... args) {
        return and(inSql(name, subSql, args).not());
    }
    
    public SqlExpressionGroup andInBySql2(String name, String subSql, Object... values) {
        return and(inSql2(name, subSql, values));
    }

    public SqlExpressionGroup andNotInBySql2(String name, String subSql, Object... values) {
        return and(inSql2(name, subSql, values).not());
    }

    public SqlExpressionGroup andNotIn(String name, long... ids) {
        return and(inLong(name, ids).not());
    }

    public SqlExpressionGroup andNotIn(String name, int... ids) {
        return and(inInt(name, ids).not());
    }

    public SqlExpressionGroup andNotIn(String name, String... names) {
        return and(inStr(name, names).not());
    }

    public SqlExpressionGroup andLike(String name, String value) {
        return and(like(name, value));
    }
    
    public SqlExpressionGroup andLikeL(String name, String value) {
        return and(like(name, value).left(null));
    }
    
    public SqlExpressionGroup andLikeR(String name, String value) {
        return and(like(name, value).right(null));
    }

    public SqlExpressionGroup andNotLike(String name, String value) {
        return and(like(name, value).not());
    }

    public SqlExpressionGroup andNotLikeL(String name, String value) {
        return and(like(name, value).left(null).not());
    }

    public SqlExpressionGroup andNotLikeR(String name, String value) {
        return and(like(name, value).right(null).not());
    }

    public SqlExpressionGroup andLike(String name, String value, boolean ignoreCase) {
        return and(like(name, value, ignoreCase));
    }

    public SqlExpressionGroup andNotLike(String name, String value, boolean ignoreCase) {
        return and(like(name, value, ignoreCase).not());
    }
    
    public SqlExpressionGroup andLike(String name, String value, String left, String right, boolean ignoreCase) {
        return and(like(name, value, ignoreCase).left(left).right(right));
    }
    
    public SqlExpressionGroup andNotLike(String name, String value, String left, String right, boolean ignoreCase) {
        return and(like(name, value, ignoreCase).left(left).right(right).not());
    }

    public SqlExpressionGroup or(String name, String op, Object value) {
        return or(Exps.create(name, op, value));
    }

    public SqlExpressionGroup or(SqlExpression exp) {
        if (exp == null) {
            if (log.isTraceEnabled())
                log.trace("ignore null SqlExpression");
            return this;
        }
        if (!exps.isEmpty())
            _add(new Static("OR"));
        return _add(exp);
    }

    public SqlExpressionGroup orEquals(String name, Object val) {
        return or(eq(name, val));
    }

    public SqlExpressionGroup orNotEquals(String name, Object val) {
        return or(eq(name, val).not());

    }

    public SqlExpressionGroup orIsNull(String name) {
        return or(isNull(name));
    }

    public SqlExpressionGroup orNotIsNull(String name) {
        return or(isNull(name).not());
    }

    public SqlExpressionGroup orGT(String name, long val) {
        return or(gt(name, val));
    }

    public SqlExpressionGroup orGTE(String name, long val) {
        return or(gte(name, val));
    }

    public SqlExpressionGroup orLT(String name, long val) {
        return or(lt(name, val));
    }

    public SqlExpressionGroup orLTE(String name, long val) {
        return or(lte(name, val));
    }

    public SqlExpressionGroup orIn(String name, long... ids) {
        return or(inLong(name, ids));
    }

    public SqlExpressionGroup orIn(String name, int... ids) {
        return or(inInt(name, ids));
    }

    public SqlExpressionGroup orIn(String name, String... names) {
        return or(inStr(name, names));
    }

    public SqlExpressionGroup orInBySql(String name, String subSql, Object... args) {
        return or(inSql(name, subSql, args));
    }

    public SqlExpressionGroup orNotInBySql(String name, String subSql, Object... args) {
        return or(inSql(name, subSql, args).not());
    }

    public SqlExpressionGroup orInBySql2(String name, String subSql, Object... values) {
        return or(inSql2(name, subSql, values));
    }

    public SqlExpressionGroup orNotInBySql2(String name, String subSql, Object... values) {
        return or(inSql2(name, subSql, values).not());
    }

    public SqlExpressionGroup orNotIn(String name, long... ids) {
        return or(inLong(name, ids).not());
    }

    public SqlExpressionGroup orNotIn(String name, int... ids) {
        return or(inInt(name, ids).not());
    }

    public SqlExpressionGroup orNotIn(String name, String... names) {
        return or(inStr(name, names).not());
    }

    public SqlExpressionGroup orLike(String name, String value) {
        return or(like(name, value));
    }
    
    public SqlExpressionGroup orLikeL(String name, String value) {
        return or(like(name, value).left(null));
    }
    
    public SqlExpressionGroup orLikeR(String name, String value) {
        return or(like(name, value).right(null));
    }

    public SqlExpressionGroup orNotLike(String name, String value) {
        return or(like(name, value).not());
    }

    public SqlExpressionGroup orNotLikeL(String name, String value) {
        return or(like(name, value).left(null).not());
    }

    public SqlExpressionGroup orNotLikeR(String name, String value) {
        return or(like(name, value).right(null).not());
    }

    public SqlExpressionGroup orLike(String name, String value, boolean ignoreCase) {
        return or(like(name, value, ignoreCase));
    }

    public SqlExpressionGroup orNotLike(String name, String value, boolean ignoreCase) {
        return or(like(name, value, ignoreCase).not());
    }
    
    public SqlExpressionGroup orLike(String name, String value, String left, String right, boolean ignoreCase) {
        return or(like(name, value, ignoreCase).left(left).right(right));
    }
    
    public SqlExpressionGroup orNotLike(String name, String value, String left, String right, boolean ignoreCase) {
        return or(like(name, value, ignoreCase).left(left).right(right).not());
    }
    
    //------------------ between
    public SqlExpressionGroup andBetween(String name, Object min, Object max) {
    	return and(new BetweenExpression(name, min, max));
    }
    
    public SqlExpressionGroup orBetween(String name, Object min, Object max) {
    	return or(new BetweenExpression(name, min, max));
    }

    @Override
    public void setPojo(Pojo pojo) {
        super.setPojo(pojo);
        for (SqlExpression exp : exps)
            exp.setPojo(pojo);
    }

    private SqlExpressionGroup _add(SqlExpression exp) {
        if (null != exp) {
            exps.add(exp);
            exp.setPojo(pojo);
            if (exp instanceof SqlExpressionGroup)
                ((SqlExpressionGroup) exp).top = false;
        }
        return this;
    }

    public void joinSql(Entity<?> en, StringBuilder sb) {
        if (!exps.isEmpty()) {
            if (top) {
                sb.append(" WHERE ");
                if (not)
                    sb.append("NOT (");
                for (SqlExpression exp : exps)
                    exp.joinSql(en, sb);
                if (not)
                    sb.append(')');
            } else {
                if (not)
                    sb.append("NOT ");
                sb.append('(');
                for (SqlExpression exp : exps)
                    exp.joinSql(en, sb);
                sb.append(')');
            }
        }
    }

    public int joinAdaptor(Entity<?> en, ValueAdaptor[] adaptors, int off) {
        for (SqlExpression exp : exps)
            off = exp.joinAdaptor(en, adaptors, off);
        return off;
    }

    public int joinParams(Entity<?> en, Object obj, Object[] params, int off) {
        for (SqlExpression exp : exps)
            off = exp.joinParams(en, obj, params, off);
        return off;
    }

    public int paramCount(Entity<?> en) {
        int re = 0;
        for (SqlExpression exp : exps)
            re += exp.paramCount(en);
        return re;
    }

    public SqlExpression setNot(boolean not) {
        this.not = not;
        return this;
    }

    public boolean isEmpty() {
        return exps.isEmpty();
    }

    public List<SqlExpression> cloneExps() {
        return new ArrayList<SqlExpression>(exps);
    }
    
    public List<SqlExpression> getExps() {
		return exps;
	}
    
    public SqlExpressionGroup clone(){
        SqlExpressionGroup seg = new SqlExpressionGroup();
        seg.exps = cloneExps();
        seg.pojo = this.pojo;
        seg.top = this.top;
        return seg;
    }
    
    /**
     * 若value为null/空白字符串/空集合/空数组,则本条件不添加.
     * @see Cnd#and(String, String, Object)
     */
    public SqlExpressionGroup andEX(String name, String op, Object value) {
        return and(Cnd.expEX(name, op, value));
    }
    
    /**
     * 若value为null/空白字符串/空集合/空数组,则本条件不添加.
     * @see Cnd#or(String, String, Object)
     */
    public SqlExpressionGroup orEX(String name, String op, Object value) {
        return or(Cnd.expEX(name, op, value));
    }
}
