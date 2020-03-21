package org.nutz.validate.impl;

import org.nutz.validate.NutValidateException;
import org.nutz.validate.NutValidator;

public class WildcardValidator implements NutValidator {

    /**
     * 查找的方式：
     * <ul>
     * <li>0: 包含
     * <li>1: 以给定字符开头
     * <li>-1: 以给定字符结尾
     * </ul>
     */
    private int mode;

    private String str;

    private String primary;

    public WildcardValidator(String s) {
        this.primary = s;
        boolean wBegin = s.startsWith("*");
        boolean wEnd = s.endsWith("*");
        // 开头匹配模式，或者包含
        if (wBegin) {
            this.mode = wEnd ? 0 : 1;
        }
        // 结尾匹配模式
        else if (wEnd) {
            this.mode = -1;
        }
        // 默认包含
        else {
            this.mode = 0;
        }
    }

    @Override
    public Object check(Object val) throws NutValidateException {
        if (null == val) {
            return val;
        }
        String v = val.toString();
        // 开头匹配模式
        if (this.mode == 1) {
            if (v.startsWith(str)) {
                return v;
            }
        }
        // 结尾匹配模式
        else if (this.mode == -1) {
            if (v.endsWith(str)) {
                return v;
            }
        }
        // 包含
        else {
            if (v.indexOf(str) >= 0) {
                return v;
            }
        }
        // 报错
        throw new NutValidateException("WildcardUnmatched", primary, val);
    }

    @Override
    public int order() {
        return 0;
    }

}
