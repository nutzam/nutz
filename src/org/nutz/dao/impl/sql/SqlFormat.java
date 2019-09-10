package org.nutz.dao.impl.sql;

import java.io.Serializable;

/**
 * Sql打印格式
 * 
 * @author 幸福的旁边(happyday517@163.com)
 */
public class SqlFormat implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 打印所有信息,参数打印10行，且参数不限长度
     */
    public static SqlFormat full = new SqlFormat(true, 10, -1, true);

    /**
     * 打印1行参数 + Example SQL
     */
    public static SqlFormat lite = new SqlFormat(true, 1, -1, true);

    /**
     * 只打印Example SQL
     */
    public static SqlFormat minimize = new SqlFormat().setPrintParam(false).setPrintExample(true);

    public SqlFormat() {
        super();
    }

    public SqlFormat(boolean printParam, int paramRowLimit, int paramLengthLimit, boolean printExample) {
        super();
        this.printParam = printParam;
        this.paramRowLimit = paramRowLimit;
        this.paramLengthLimit = paramLengthLimit;
        this.printExample = printExample;
    }

    private boolean printParam;// 是否打印参数矩阵
    private int paramRowLimit;// 参数矩阵行数限制 仅当printParam=true时有效
    private int paramLengthLimit;//参数矩阵字段长度限制 仅当printParam=true时有效
    private boolean printExample;//是否打印For example

    public boolean isPrintParam() {
        return printParam;
    }

    public SqlFormat setPrintParam(boolean printParam) {
        this.printParam = printParam;
        return this;
    }

    public int getParamRowLimit() {
        return paramRowLimit;
    }

    public SqlFormat setParamRowLimit(int paramRowLimit) {
        this.paramRowLimit = paramRowLimit;
        return this;
    }

    public int getParamLengthLimit() {
        return paramLengthLimit;
    }

    public SqlFormat setParamLengthLimit(int paramLengthLimit) {
        this.paramLengthLimit = paramLengthLimit;
        return this;
    }

    public boolean isPrintExample() {
        return printExample;
    }

    public SqlFormat setPrintExample(boolean printExample) {
        this.printExample = printExample;
        return this;
    }

    @Override
    public SqlFormat clone() {
        return new SqlFormat(this.printParam, this.paramRowLimit, this.paramLengthLimit, this.printExample);
    }
}