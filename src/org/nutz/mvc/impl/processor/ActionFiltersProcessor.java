package org.nutz.mvc.impl.processor;

import java.util.ArrayList;
import java.util.List;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.ObjectInfo;
import org.nutz.mvc.Processor;
import org.nutz.mvc.View;

/**
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class ActionFiltersProcessor extends AbstractProcessor {

    protected List<ActionFilter> filters = new ArrayList<ActionFilter>();
    
    protected Processor proxyProcessor;
    
    protected Processor lastProcessor;
    
    public void init(NutConfig config, ActionInfo ai) throws Throwable {
        ObjectInfo<? extends ActionFilter>[] filterInfos = ai.getFilterInfos();
        if (null != filterInfos) {
            for (int i = 0; i < filterInfos.length; i++) {
            	ActionFilter filter = evalObj(config, filterInfos[i]);
                filters.add(filter);
                if (filter instanceof Processor) {
            		Processor processor = (Processor)filter;
                	if (proxyProcessor == null) {
                		proxyProcessor = processor;
                		lastProcessor = processor;
                	} else {
                		processor.setNext(proxyProcessor);
                		proxyProcessor = processor;
                	}
                }
            }
        }
    }

    public void process(ActionContext ac) throws Throwable {
        for (ActionFilter filter : filters) {
            View view = filter.match(ac);
            if (null != view) {
                ac.setMethodReturn(view);
                renderView(ac);
                return;
            }
        }
        if (proxyProcessor == null) {
        	doNext(ac);
        } else {
        	if (lastProcessor != null)
        		lastProcessor.setNext(next);
        	proxyProcessor.process(ac);
        }
    }
}
