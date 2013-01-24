package org.nutz.http;

import java.net.Proxy;
import java.net.URL;

public interface ProxySwitcher {

	Proxy getProxy(URL url);
}
