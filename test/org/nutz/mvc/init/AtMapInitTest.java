package org.nutz.mvc.init;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.nutz.mvc.AbstractMvcTest;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.config.AtMap;
import org.nutz.mvc.init.conf.MainModuleAtMap;

public class AtMapInitTest extends AbstractMvcTest {

    @Override
    protected void initServletConfig() {
        servletConfig.addInitParameter("modules", MainModuleAtMap.class.getName());
    }

    @Test
    public void test_at_map() throws ServletException, IOException {
        request.setPathInfo("/atmap/ABC");
        servlet.service(request, response);
        assertEquals("\">>abc\"", response.getAsString());

        newreq();
        request.setPathInfo("/atmap/xyz");
        servlet.service(request, response);
        assertEquals("\">>xyz\"", response.getAsString());
        
        Mvcs.set("nutz", request, response);
        
        AtMap am = Mvcs.getAtMap();
        assertEquals("/atmap/ABC", am.get("at.abc"));
        assertEquals("/atmap/xyz", am.get("at.xyz"));
    }

}
