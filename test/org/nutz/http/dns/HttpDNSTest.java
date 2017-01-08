package org.nutz.http.dns;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.http.dns.impl.AliDnsProvider;

public class HttpDNSTest {

    @Test
    public void testSetProvider() {
        
        AliDnsProvider provider = new AliDnsProvider("142444");
        HttpDNS.setProvider(provider);
        assertNotNull(HttpDNS.getIp("cdn.nutz.cn"));
        
        Response resp = Http.get("http://cdn.nutz.cn/robots.txt");
        resp.isOK();
        //assertTrue(resp.isOK());
        
        //Response resp = Http.get("http://www.aliyun.com");
        //assertTrue(resp.isOK());
    }

}
