package org.nutz.validate.impl;

import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.Each;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.validate.NutValidateException;
import org.nutz.validate.NutValidator;

public class StrEnumValidator implements NutValidator {

    private String[] strs;

    public StrEnumValidator(Object any) {
        // 拆掉字符串
        if (any instanceof CharSequence) {
            strs = Strings.splitIgnoreBlank(any.toString());
        }
        // 其他的循环一下
        else {
            // 全都变成字符串
            final List<String> list = new ArrayList<String>();
            Lang.each(any, new Each<Object>() {
                public void invoke(int index, Object ele, int length) {
                    list.add(ele.toString());
                }
            });
            // 整理成数组
            strs = list.toArray(new String[list.size()]);
        }
    }

    @Override
    public Object check(Object val) throws NutValidateException {
        if (null == val) {
            return val;
        }
        String s = val.toString();
        for (String str : strs) {
            if (str.equals(s)) {
                return s;
            }
        }
        throw new NutValidateException("StrOutOfEnum", strs.toString(), val);
    }

    @Override
    public int order() {
        return 1000;
    }

}
