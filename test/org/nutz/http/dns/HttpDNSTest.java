package org.nutz.http.dns;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.http.dns.impl.AliDnsProvider;

public class HttpDNSTest {

    @Test
    public void testSetProvider() {
        
        AliDnsProvider provider = new AliDnsProvider("100000");
        HttpDNS.setProvider(provider);
        assertNotNull(HttpDNS.getIp("www.aliyun.com"));
        
        Response resp = Http.get("http://www.aliyun.com");
        assertTrue(resp.isOK());
        
        //Response resp = Http.get("http://www.aliyun.com");
        //assertTrue(resp.isOK());
    }

}
