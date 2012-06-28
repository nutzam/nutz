package org.nutz.mvc.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.mvc.AbstractMvcTest;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.impl.processor.ViewProcessor;
import org.nutz.mvc.view.VoidView;

public class ViewProcessorTest extends AbstractMvcTest {

    @Test
    public void test_error_processor() throws Throwable {
        ViewProcessor p = new EViewProcessor();
        ActionContext ac = new ActionContext();
        ac.setRequest(request).setResponse(response).setServletContext(servletContext);
        Throwable t = new Throwable();
        ac.setError(t);
        p.process(ac);
        Object obj = request.getAttribute(ViewProcessor.DEFAULT_ATTRIBUTE);
        assertNotNull(obj);
        assertTrue(obj instanceof Throwable);
        assertEquals(t, obj);
    }

    @Override
    protected void initServletConfig() {
        servletConfig.addInitParameter("modules", "org.nutz.mvc.init.module.MainModule");
    }
    
}

class EViewProcessor extends ViewProcessor {
    
    public EViewProcessor() {
        view = new VoidView();
    }
}