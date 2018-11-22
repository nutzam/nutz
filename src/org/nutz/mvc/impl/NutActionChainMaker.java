package org.nutz.mvc.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
    
    private static final Log log = Logs.get();
    
    ActionChainMakerConfiguration co;
    
    protected ConcurrentHashMap<String, String> disabledProcessor = new ConcurrentHashMap<String, String>();
    
    public NutActionChainMaker(String...args) {
        co = new JsonActionChainMakerConfiguretion(args);
    }

    public ActionChain eval(NutConfig config, ActionInfo ai) {
        
        try {
            List<Processor> list = new ArrayList<Processor>();
            for (String name : co.getProcessors(ai.getChainName())) {
                Processor processor = getProcessorByName(config, name);
                if (processor != null) {
                    processor.init(config, ai);
                    list.add(processor);
                }
            }

            Processor errorProcessor = getProcessorByName(config, co.getErrorProcessor(ai.getChainName()));
            errorProcessor.init(config, ai);
            /*
             * 返回动作链实例
             */
            return new NutActionChain(list, errorProcessor, ai);
        } catch (Throwable e) {
            if (log.isDebugEnabled())
                log.debugf("Eval FAIL!! : %s",ai.getMethod(), e);
            throw Lang.wrapThrow(e);
        }
    }

    protected Processor getProcessorByName(NutConfig config,String name) throws Exception {
        if (name.startsWith("ioc:") && name.length() > 4) {
            if (config.getIoc() == null)
                throw new IllegalArgumentException("getProcessorByName " + name + " but no ioc !");
            return config.getIoc().get(Processor.class, name.substring(4).trim());
        }
        else {
            Class<?> klass = null;
            if (name.startsWith("!")) {
                name = name.substring(1);
                if (disabledProcessor.contains(name))
                    return null;
                try {
                    klass = Lang.loadClass(name);
                }
                catch (Throwable e) {
                    log.info("Optional processor class not found, disabled : " + name);
                    disabledProcessor.put(name, name);
                    return null;
                }
                return (Processor) Mirror.me(klass).born();
            }
            return (Processor) Mirror.me(Lang.loadClass(name)).born();
        }
    }
}
