package org.nutz.dao.util.cri;

import java.util.Collection;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;

/**
 * 表达式的帮助函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Exps {

    public static SqlExpressionGroup begin() {
        return new SqlExpressionGroup();
    }

    public static Like like(String name, String value) {
        return Like.create(name, value, true);
    }

    public static Like like(String name, String value, boolean ignoreCase) {
        return Like.create(name, value, ignoreCase);
    }

    public static IsNull isNull(String name) {
        return new IsNull(name);
    }

    public static SimpleExpression eq(String name, Object val) {
        return new SimpleExpression(name, "=", val);
    }

    public static SimpleExpression gt(String name, long val) {
        return new SimpleExpression(name, ">", val);
    }

    public static SimpleExpression lt(String name, long val) {
        return new SimpleExpression(name, "<", val);
    }

    public static SimpleExpression gte(String name, long val) {
        return new SimpleExpression(name, ">=", val);
    }

    public static SimpleExpression lte(String name, long val) {
        return new SimpleExpression(name, "<=", val);
    }

    public static IntRange inInt(String name, int... ids) {
        return new IntRange(name, ids);
    }

    public static LongRange inLong(String name, long... ids) {
        return new LongRange(name, ids);
    }

    public static NameRange inStr(String name, String... names) {
        return new NameRange(name, names);
    }

    public static SqlRange inSql(String name, String subSql, Object... args) {
        return new SqlRange(name, subSql, args);
    }

    public static SqlExpression create(String name, String op, Object value) {
        op = Strings.trim(op.toUpperCase());

        // NULL
        if (null == value) {
            SqlExpression re;
            // IS NULL
            if ("=".equals(op) || "IS".equals(op) || "NOT IS".equals(op) || "IS NOT".equals(op)) {
                re = isNull(name);
            }
            // !!!
            else {
                throw Lang.makeThrow("null can only use 'IS' or 'NOT IS'");
            }
            return re.setNot(op.startsWith("NOT") || op.endsWith("NOT"));
        }
        // IN
        else if ("IN".equals(op) || "NOT IN".equals(op)) {
            Class<?> type = value.getClass();
            SqlExpression re;
            // 数组
            if (type.isArray()) {
                re = _evalRange((Mirror<?>) Mirror.me(type.getComponentType()), name, value);
            }
            // 集合
            else if (Collection.class.isAssignableFrom(type)) {
                Object first = Lang.first(value);
                if (null == first)
                    return null;
                re = _evalRange((Mirror<?>) Mirror.me(first), name, value);
            }
            // Sql Range
            else {
                re = inSql(name, value.toString());
            }
            return re.setNot(op.startsWith("NOT"));
        }
        // LIKE || IS
        else if ("LIKE".equals(op) || "NOT LIKE".equals(op)) {
            String v = value.toString();
            Like re;
            if (v.length() == 1) {
                re = like(name, v);
            } else {
                re = like(name, v.substring(1, v.length() - 1));
                re.left(v.substring(0, 1));
                re.right(v.substring(v.length() - 1, v.length()));
            }
            return re.ignoreCase(false).setNot(op.startsWith("NOT"));
        }
        // =
        else if ("=".equals(op)) {
            return eq(name, value);
        }
        // !=
        else if ("!=".equals(op) || "<>".equals(op)) {// TODO 检查一下,原本是&&, 明显永远成立
            return eq(name, value).setNot(true);
        }
        // Others
        return new SimpleExpression(name, op, value);
    }

    private static SqlExpression _evalRange(Mirror<?> mirror, String name, Object value) {
        if (mirror.isInt())
            return inInt(name, Castors.me().castTo(value, int[].class));

        else if (mirror.isLong())
            return inLong(name, Castors.me().castTo(value, long[].class));

        return inStr(name, Castors.me().castTo(value, String[].class));
    }

}
