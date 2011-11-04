package org.nutz.resource;

import java.util.Map;

import org.junit.Test;
import org.nutz.resource.NutConfig;

import static org.junit.Assert.*;

/**
 * 配置器
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class NutConfigTest {
    
    @Test
    public void simpleNullTypeTest(){
        NutConfig.load("config/test.js");
        Map<?, ?> conf = (Map<?, ?>) NutConfig.get("TEST");
        assertEquals("jk", conf.get("name"));
    }
    
    @Test
    public void simpleTest(){
        NutConfig.load("config/test.js");
        Map<?, ?> conf = (Map<?, ?>) NutConfig.get("TEST", Map.class);
        assertEquals("jk", conf.get("name"));
    }
    
    @Test
    public void pathTest(){
        NutConfig.load("config");
        Map<?, ?> conf = (Map<?, ?>) NutConfig.get("TEST", Map.class);
        assertEquals("jk", conf.get("name"));
    }
    
    @Test
    public void loadDefaultConfigTest(){
        NutConfig.load("config/NutzDefaultConfig.js");
        Map<?, ?> conf = (Map<?, ?>) NutConfig.get("TEST", Map.class);
        assertNotNull(conf);
    }
    
    @Test
    public void includeTest(){
        NutConfig.load("config/include.js");
        Map<?, ?> conf = (Map<?, ?>) NutConfig.get("TEST", Map.class);
        assertEquals("jk", conf.get("name"));
    }
}
