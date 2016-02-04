package org.nutz.mvc.impl.processor;

import java.util.ArrayList;
import java.util.List;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.ActionFilter2;
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

    protected List<ActionFilter> filters = new ArrayList<ActionFilter>();
    
    protected List<ActionFilter2> filters2 = new ArrayList<ActionFilter2>();
    
    public void init(NutConfig config, ActionInfo ai) throws Throwable {
        ObjectInfo<? extends ActionFilter>[] filterInfos = ai.getFilterInfos();
        if (null != filterInfos) {
            for (int i = 0; i < filterInfos.length; i++) {
            	ActionFilter filter = evalObj(config, filterInfos[i]);
                filters.add(filter);
                if (filter instanceof ActionFilter2) {
                	filters2.add(0, (ActionFilter2)filter);
                }
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
        for (ActionFilter2 filter2 : filters2) {
			filter2.after(ac);
		}
    }

}
