package org.nutz.http.dns;

public class HttpDNS {
    
    protected static HttpDnsProvider provider;

    public static void setProvider(HttpDnsProvider provider) {
        HttpDNS.provider = provider;
        if (provider != null)
            System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
    }

    public static String getIp(String hostname) {
        if (provider != null)
            return provider.getIp(hostname, 15*1000);
        return null;
    }
    
    public static void clear() {
        if (provider != null)
            provider.clear();
    }
}
