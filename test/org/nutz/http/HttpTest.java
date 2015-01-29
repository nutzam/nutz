package org.nutz.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.junit.Test;
import org.nutz.Nutz;
import org.nutz.http.Request.METHOD;
import org.nutz.json.Json;

public class HttpTest {

    @Test
    public void testGet() {
        Response response = Http.get("http://nutztest.herokuapp.com/");
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
        String response = Http.post("http://nutztest.herokuapp.com/",
                                    parms,
                                    5 * 1000);
        assertNotNull(response);
        assertTrue(response.length() > 0);
        // 该post的返回值是"version: #{params[:version]}, website: #{params[:website]}"
        assertEquals(response,
                     String.format("version: %s, website: %s",
                                   "NutzTest",
                                   Nutz.version()));
    }

    @Test
    public void testEncode() {
        // 根据Http头的Content-Type自动识别编码类型
        Response response1 = Http.get("www.duokan.com");
        assertEquals("utf-8", response1.getEncodeType().toLowerCase());
        assertTrue(response1.getContent().indexOf("多看") > 0);

        // 如果Http头中没有指定编码类型，用户也可以手工指定
        Response response2 = Http.get("www.exam8.com/SiteMap/Article1.htm");
        assertTrue(response2.getContent("GBK").indexOf("考试吧") > 0);
    }

    @Test(expected = HttpException.class)
    public void testTimeout() {
        Response response = Http.get("www.baidu.com", 10 * 1000);
        assertTrue(response.getStatus() == 200);
        // 抛出超时异常
        Http.get("www.baidu.com", 1);
    }

    @Test
    public void test_https() {
        Response response = Http.get("https://github.com");
        assertTrue(response.getStatus() == 200);
    }

    @Test
    public void test_chunked_content() {
        Response response = Http.get("http://app.danoolive.com:8201/test");
        assertEquals(200, response.getStatus());
        assertEquals("chunked", response.getHeader().get("Transfer-Encoding"));
        assertEquals("OK", response.getContent());
    }
    
    @Test
    public void test_getBoundary() {
    	String boundary = Http.multipart.getBoundary("multipart/form-data; charset=utf-8; boundary=0xKhTmLbOuNdArY-1593BCBB-3B9B-433B-8BC0-4B768CDA81CF");
    	assertEquals("0xKhTmLbOuNdArY-1593BCBB-3B9B-433B-8BC0-4B768CDA81CF", boundary);
    	boundary = Http.multipart.getBoundary("multipart/form-data; charset=utf-8; boundary=0xKhTmLbOuNdArY-5DA6CD94-6D26-4C89-9F8D-09307F3A6F97");
    	assertEquals("0xKhTmLbOuNdArY-5DA6CD94-6D26-4C89-9F8D-09307F3A6F97", boundary);
    }
    
    @Test
    public void test_yeelink() {
        //String key = "f7bd63b34b30303a11a36f6fd7628ef4";
        String device_id = "12825";
        String sensor_id = "20872";
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("value", 31.3);
        String url = String.format("http://api.yeelink.net/v1.1/device/%s/sensor/%s/datapoints", device_id, sensor_id);
        System.out.println("URL="+url);
        Request req = Request.create(url, METHOD.POST);
        req.setData(Json.toJson(data));
        //req.getHeader().set("U-ApiKey", key);
        Response resp = Sender.create(req).send();
        System.out.println(resp.getStatus());
    }
    
    @Test
    public void test_12306() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSL");
        TrustManager[] tmArr={new X509TrustManager(){
            public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate,String paramString) throws CertificateException{}
            public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate,String paramString) throws CertificateException {}
            public X509Certificate[] getAcceptedIssuers() {return null;}
        }};
        sc.init(null, tmArr, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        
        String url = "https://kyfw.12306.cn/otn/leftTicket/queryT?leftTicketDTO.train_date=2015-01-12&leftTicketDTO.from_station=UXP&leftTicketDTO.to_station=SJP&purpose_codes=ADULT";
        Response response = Http.get(url);
        System.out.println(response.getContent());
    }
}
