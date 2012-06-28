package org.nutz.http;

import static org.junit.Assert.*;

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
