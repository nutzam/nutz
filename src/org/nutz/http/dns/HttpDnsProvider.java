package org.nutz.http.dns;

public interface HttpDnsProvider {

    String getIp(String host, int timeout);
    
    void clear();

}
