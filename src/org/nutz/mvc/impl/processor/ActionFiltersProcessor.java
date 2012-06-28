package org.nutz.mvc.impl.processor;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.ObjectInfo;
import org.nutz.mvc.View;

/**
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class ActionFiltersProcessor extends AbstractProcessor {

    private ActionFilter[] filters = {};
    
    public void init(NutConfig config, ActionInfo ai) throws Throwable {
        ObjectInfo<? extends ActionFilter>[] filterInfos = ai.getFilterInfos();
        if (null != filterInfos) {
            filters = new ActionFilter[filterInfos.length];
            for (int i = 0; i < filters.length; i++) {
                filters[i] = evalObj(config, filterInfos[i]);
            }
        }
    }

    public void process(ActionContext ac) throws Throwable {
        View view;
        for (ActionFilter filter : filters) {
            view = filter.match(ac);
            if (null != view) {
                ac.setMethodReturn(view);
                renderView(ac);
                return;
            }
        }
        doNext(ac);
    }

}
