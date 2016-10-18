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
        assertNotNull(HttpDNS.getIp("xplay.io"));
        
        Response resp = Http.get("http://xplay.io");
        assertTrue(resp.isOK());
        
        //Response resp = Http.get("http://www.aliyun.com");
        //assertTrue(resp.isOK());
    }

}
