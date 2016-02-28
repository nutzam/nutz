package org.nutz.el.opt.custom;

import org.nutz.el.opt.RunMethod;
import org.nutz.plugin.Plugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 时间函数
 * Created by wendal on 2015/12/22.
 */
public class TimeNow implements RunMethod, Plugin {

    public boolean canWork() {
        return true;
    }

    public Object run(List<Object> fetchParam) {
        if (fetchParam == null || fetchParam.isEmpty())
            return System.currentTimeMillis();
        return new SimpleDateFormat(fetchParam.get(0).toString()).format(new Date());
    }

    public String fetchSelf() {
        return "now";
    }
}
