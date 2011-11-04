package org.nutz.resource;

import java.util.Map;

import org.junit.Test;
import org.nutz.conf.NutConf;

import static org.junit.Assert.*;

/**
 * 配置器
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class NutConfigTest {
    
    @Test
    public void nullConfigTest(){
      //加载nutz配置
        NutConf.load();
    }
    
    @Test
    public void simpleNullTypeTest(){
        NutConf.load("config/test.js");
        Map<?, ?> conf = (Map<?, ?>) NutConf.get("TEST");
        assertEquals("jk", conf.get("name"));
    }
    
    @Test
    public void simpleTest(){
        NutConf.load("config/test.js");
        Map<?, ?> conf = (Map<?, ?>) NutConf.get("TEST", Map.class);
        assertEquals("jk", conf.get("name"));
    }
    
    @Test
    public void pathTest(){
        NutConf.load("config");
        Map<?, ?> conf = (Map<?, ?>) NutConf.get("TEST", Map.class);
        assertEquals("jk", conf.get("name"));
    }
    
    @Test
    public void loadDefaultConfigTest(){
        NutConf.load("config/NutzDefaultConfig.js");
        Map<?, ?> conf = (Map<?, ?>) NutConf.get("TEST", Map.class);
        assertNotNull(conf);
    }
    
    @Test
    public void includeTest(){
        NutConf.load("config/include.js");
        Map<?, ?> conf = (Map<?, ?>) NutConf.get("TEST", Map.class);
        assertEquals("jk", conf.get("name"));
    }
}
