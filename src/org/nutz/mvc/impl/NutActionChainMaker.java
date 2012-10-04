package org.nutz.mvc.impl;

import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionChain;
import org.nutz.mvc.ActionChainMaker;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Processor;
import org.nutz.mvc.impl.chainconfig.ActionChainMakerConfiguration;
import org.nutz.mvc.impl.chainconfig.JsonActionChainMakerConfiguretion;

public class NutActionChainMaker implements ActionChainMaker {
    
    private static final Log logger = Logs.get();
    
    ActionChainMakerConfiguration co;
    
    public NutActionChainMaker(String...args) {
        co = new JsonActionChainMakerConfiguretion(args);
    }

    public ActionChain eval(NutConfig config, ActionInfo ai) {
        
        try {
            List<Processor> list = new ArrayList<Processor>();
            for (String name : co.getProcessors(ai.getChainName())) {
                Processor processor = getProcessorByName(config, name);
                processor.init(config, ai);
                list.add(processor);
            }

            Processor errorProcessor = getProcessorByName(config, co.getErrorProcessor(ai.getChainName()));
            errorProcessor.init(config, ai);
            /*
             * 返回动作链实例
             */
            ActionChain chain = new NutActionChain(list, errorProcessor, ai.getMethod());
            return chain;
        } catch (Throwable e) {
            if (logger.isDebugEnabled())
                logger.debugf("Eval FAIL!! : %s",ai.getMethod());
            throw Lang.wrapThrow(e);
        }
    }

    protected static Processor getProcessorByName(NutConfig config,String name) throws Exception {
        if (name.startsWith("ioc:") && name.length() > 4)
            return config.getIoc().get(Processor.class, name.substring(4));
        else
            return (Processor) Mirror.me(Lang.loadClass(name)).born();
    }
}
