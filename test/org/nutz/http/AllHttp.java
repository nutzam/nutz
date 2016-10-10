package org.nutz.http;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.http.dns.HttpDNSTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({HttpTest.class, HttpDNSTest.class})
public class AllHttp {}
