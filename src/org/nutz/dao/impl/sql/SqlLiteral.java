package org.nutz.dao.impl.sql;

import java.io.Serializable;

import org.nutz.dao.sql.SqlType;
import org.nutz.dao.sql.VarIndex;
import org.nutz.lang.Strings;

/**
 * @author zozoh
 * @author wendal(wendal1985@gmail.com)
 */
public class SqlLiteral implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    WorkingStack stack;

    private VarIndexImpl varIndexes;

    private VarIndexImpl paramIndexes;

    private String source;

    private SqlType type;
    
    private char paramChar;
    
    private char varChar;

    public SqlLiteral() {
        this('@', '$'); // TODO 变成静态属性供用户设置?
    }
    public SqlLiteral(char paramChar, char varChar) {
        this.paramChar = paramChar;
        this.varChar = varChar;
    }

    private void reset() {
        stack = new WorkingStack();
        varIndexes = new VarIndexImpl();
        paramIndexes = new VarIndexImpl();
        // statementIndexes = new VarIndexImpl();
    }

    VarIndex getVarIndexes() {
        return varIndexes;
    }

    VarIndex getParamIndexes() {
        return paramIndexes;
    }

    /**
     * [@|$][a-zA-Z0-9_-.]+
     * 
     * <pre>
     * 48-57    0-9
     * 65-90    A-Z
     * 97-122    a-z
     * 95        _
     * 45        -
     * 46        .
     * </pre>
     * 
     * @param str
     * @return SqlLiteral
     */
    SqlLiteral valueOf(String str) {
        reset();
        // int statementIndex = 1;
        source = str;
        if (null == source)
            return this;
        char[] cs = Strings.trim(source).toCharArray();
        StringBuilder sb;
        for (int i = 0; i < cs.length; i++) {
            char c = cs[i];
            if (c == paramChar) {
                if (cs[i + 1] == c) {
                    stack.push(c);
                    i++;
                    continue;
                }
                sb = new StringBuilder();
                i = readTokenName(cs, i, sb);
                // Fail to read token name
                if (sb.length() == 0) {
                    stack.push(c);
                } else {
                    paramIndexes.add(sb.toString(), stack.markToken());
                    // paramIndexes.add(name, stack.markToken());
                    // statementIndexes.add(name, statementIndex++);
                }
            }
            else if (c == varChar) {
                if (cs[i + 1] == varChar) {
                    stack.push(c);
                    i++;
                    continue;
                }
                sb = new StringBuilder();
                i = readTokenName(cs, i, sb);
                // Fail to read token name
                if (sb.length() == 0) {
                    stack.push(c);
                } else {
                    // varIndexes.add(sb.toString(), stack.markToken());
                    varIndexes.add(sb.toString(), stack.markToken());
                }
            }
            else {
                stack.push(c);
            }
        }
        stack.finish();

        // eval SqlType ...

        if (stack.firstEquals("SELECT") || stack.firstEquals("WITH"))
            type = SqlType.SELECT;
        else if (stack.firstEquals("UPDATE"))
            type = SqlType.UPDATE;
        else if (stack.firstEquals("INSERT"))
            type = SqlType.INSERT;
        else if (stack.firstEquals("DELETE"))
            type = SqlType.DELETE;
        else if (stack.firstEquals("CREATE"))
            type = SqlType.CREATE;
        else if (stack.firstEquals("DROP"))
            type = SqlType.DROP;
        else if (stack.firstEquals("TRUNCATE"))
            type = SqlType.TRUNCATE;
        else if (stack.firstEquals("ALTER"))
            type = SqlType.ALTER;
        else if (stack.firstEquals("EXEC"))
            type = SqlType.EXEC;
        else if (stack.firstEquals("CALL"))
            type = SqlType.CALL;
        else if (stack.firstEquals("{CALL"))
            type = SqlType.CALL;
        else
            type = SqlType.OTHER;

        return this;
    }

    private static int readTokenName(char[] cs, int i, StringBuilder sb) {
        for (++i; i < cs.length; i++) {
            int b = (int) cs[i];
            // Special case for underline ('_')
            if (b == 95) {
                sb.append((char) b);
            }
            // 遇到了 '$'
            else if (b == 36) {
                return i;
            }
            // 正常的不可忽略的字符
            else if ((b >= 0 && b <= 47)
                        || (b >= 58 && b <= 64)
                        || (b >= 91 && b <= 96)
                        || (b >= 123 && b <= 160)) {
                break;
            } else {
                sb.append((char) b);
            }
        }
        return i - 1;
    }

    @Override
    public SqlLiteral clone() {
        return new SqlLiteral(paramChar, varChar).valueOf(source);
    }

    public String toString() {
        return source;
    }

    boolean isSELECT() {
        return SqlType.SELECT == type;
    }

    boolean isUPDATE() {
        return SqlType.UPDATE == type;
    }

    boolean isINSERT() {
        return SqlType.INSERT == type;
    }

    boolean isDELETE() {
        return SqlType.DELETE == type;
    }

    boolean isCREATE() {
        return SqlType.CREATE == type;
    }

    boolean isDROP() {
        return SqlType.DROP == type;
    }

    boolean isTRUNCATE() {
        return SqlType.TRUNCATE == type;
    }

    SqlType getType() {
        return type;
    }

}
