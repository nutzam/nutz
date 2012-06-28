package org.nutz.mvc.impl.processor;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;

/**
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class EncodingProcessor extends AbstractProcessor{

    private String input;
    private String output;
    
    @Override
    public void init(NutConfig config, ActionInfo ai) throws Throwable {
        input = ai.getInputEncoding();
        output = ai.getOutputEncoding();
    }

    public void process(ActionContext ac) throws Throwable {
        ac.getRequest().setCharacterEncoding(input);
        ac.getResponse().setCharacterEncoding(output);
        doNext(ac);
    }

}
