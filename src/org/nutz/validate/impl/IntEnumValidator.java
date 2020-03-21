package org.nutz.validate.impl;

import java.util.ArrayList;
import java.util.List;

import org.nutz.castor.Castors;
import org.nutz.lang.Each;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.validate.NutValidateException;
import org.nutz.validate.NutValidator;

public class IntEnumValidator implements NutValidator {

    private int[] nbs;

    public IntEnumValidator(Object any) {
        // 拆掉字符串
        if (any instanceof CharSequence) {
            any = Strings.splitIgnoreBlank(any.toString());
        }
        // 全都变成数字
        final List<Integer> list = new ArrayList<Integer>();
        Lang.each(any, new Each<Object>() {
            public void invoke(int index, Object ele, int length) {
                int n = Castors.me().castTo(ele, Integer.class);
                list.add(n);
            }
        });
        // 整理成数组
        nbs = new int[list.size()];
        int i = 0;
        for (int n : list) {
            nbs[i++] = n;
        }
    }

    @Override
    public Object check(Object val) throws NutValidateException {
        if(null == val) {
            return val;
        }
        int v = Castors.me().castTo(val, Integer.class);
        for (int n : nbs) {
            if (n == v) {
                return v;
            }
        }
        throw new NutValidateException("IntOutOfEnum", nbs.toString(), val);
    }

    @Override
    public int order() {
        return 1000;
    }

}
