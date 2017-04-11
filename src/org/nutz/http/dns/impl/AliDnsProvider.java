package org.nutz.http.dns.impl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.http.dns.HttpDnsProvider;
import org.nutz.json.Json;
import org.nutz.lang.Encoding;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class AliDnsProvider implements HttpDnsProvider {

    private static final Log log = Logs.get().setTag("alidns");

    protected ConcurrentHashMap<String, List<String>> ips = new ConcurrentHashMap<String, List<String>>();

    protected String uid;

    public AliDnsProvider(String uid) {
        this.uid = uid;
    }

    public String getIp(String host, int timeout) {
        List<String> ipList = ips.get(host);
        if (ipList == null) {
            try {
                Response resp = Http.get(String.format("http://203.107.1.1/%s/d?host=%s",
                                                       uid,
                                                       host),
                                         timeout);
                if (resp.isOK()) {
                    ipList = Json.fromJson(NutMap.class, resp.getReader(Encoding.UTF8))
                                 .getList("ips", String.class);
                    ips.put(host, ipList);
                    log.debugf("alidns host=[%s] ips=%s", host, ipList);
                }
            }
            catch (Throwable e) {
                log.info("alidns fail host=" + host, e);
            }
        }
        if (ipList != null) {
            if (ipList.isEmpty())
                return null;
            return ipList.get(0);
        }

        return null;
    }

    public void clear() {
        ips.clear();
    }

}
