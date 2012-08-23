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
        NutConf.load("org/nutz/conf/NutzDefaultConfig.js");
        Map<?, ?> conf = (Map<?, ?>) NutConf.get("TEST", Map.class);
        assertNull(conf); /*
        by wendal:
            原本写的是assertNotNull,从TestCase的角度看,是错误的.
            然而,如果改成assertNull, 单独跑这个测试是pass,但如果本类中其他TestCase先跑,那么这个测试必然fail
            原因在于NutConf并不清除之前的配置信息, 建议加个NutConf.clear(), 然后在每个TestCase之前运行(即onBefore)
        */
    }
    
    @Test
    public void includeTest(){
        NutConf.load("config/include.js");
        Map<?, ?> conf = (Map<?, ?>) NutConf.get("TEST", Map.class);
        assertEquals("jk", conf.get("name"));
    }
}
