package org.nutz.http;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.nutz.Nutz;

public class HttpTest {

    @Test
    public void testGet() {
        Response response = Http.get("http://nutzam.com");
        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getDetail());
        assertNotNull(response.getHeader());
        assertNotNull(response.getProtocal());
        assertTrue(response.getStatus() > 0);
        assertNotNull(response.getStream());
    }
    
    @Test
    public void testEncode() {
        //根据Http头的Content-Type自动识别编码类型
        Response response1 = Http.get("www.baidu.com");
        assertTrue("gbk".equals(response1.getEncodeType()));
        assertTrue(response1.getContent().indexOf("百度") > 0);
        //如果Http头中没有指定编码类型，用户也可以手工指定
        Response response2 = Http.get("www.exam8.com/SiteMap/Article1.htm");
        assertTrue(response2.getContent("GBK").indexOf("考试吧") > 0);
    }
    
    @Test(expected = HttpException.class)
    public void testTimeout() {
    	Response response = Http.get("www.baidu.com", 10 * 1000);
    	assertTrue(response.getStatus() == 200);
    	//抛出超时异常
    	Http.get("www.baidu.com", 1);
    }

    @Test
    public void testPost() {
        Map<String, Object> parms = new HashMap<String, Object>();
        parms.put("version", "NutzTest");
        parms.put("website", Nutz.version());
        String response = Http.post("http://p.sunfarms.net/muchang/ping.php",parms,"utf-8","utf-8");
        assertNotNull(response);
        assertTrue(response.length() > 0);
        assertTrue(response.indexOf("OK") > -1);
    }
}
