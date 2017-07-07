package org.nutz.http;

import static org.junit.Assert.*;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.nutz.Nutz;

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

//    @Test
//    public void testEncode() {
//        // 根据Http头的Content-Type自动识别编码类型
//        Response response1 = Http.get("www.duokan.com");
//        assertEquals("utf-8", response1.getEncodeType().toLowerCase());
//        assertTrue(response1.getContent().indexOf("多看") > 0);
//
//        // 如果Http头中没有指定编码类型，用户也可以手工指定
//        //Response response2 = Http.get("www.exam8.com/SiteMap/Article1.htm");
//        //assertTrue(response2.getContent("GBK").indexOf("考试吧") > 0);
//    }

//    @Test(expected = HttpException.class)
//    public void testTimeout() {
//        Response response = Http.get("www.baidu.com", 10 * 1000);
//        assertTrue(response.getStatus() == 200);
//        // 抛出超时异常
//        Http.get("www.sina.com.cn", 1);
//    }

    @Test
    public void test_https() {
        Response response = Http.get("https://nutz.cn");
        assertTrue(response.getStatus() == 200);
    }

    
    @Test
    public void test_getBoundary() {
    	String boundary = Http.multipart.getBoundary("multipart/form-data; charset=utf-8; boundary=0xKhTmLbOuNdArY-1593BCBB-3B9B-433B-8BC0-4B768CDA81CF");
    	assertEquals("0xKhTmLbOuNdArY-1593BCBB-3B9B-433B-8BC0-4B768CDA81CF", boundary);
    	boundary = Http.multipart.getBoundary("multipart/form-data; charset=utf-8; boundary=0xKhTmLbOuNdArY-5DA6CD94-6D26-4C89-9F8D-09307F3A6F97");
    	assertEquals("0xKhTmLbOuNdArY-5DA6CD94-6D26-4C89-9F8D-09307F3A6F97", boundary);
    }
    
//    @Test
//    public void test_yeelink() {
//        //String key = "f7bd63b34b30303a11a36f6fd7628ef4";
//        String device_id = "12825";
//        String sensor_id = "20872";
//        Map<String, Object> data = new HashMap<String, Object>();
//        data.put("value", 31.3);
//        String url = String.format("http://api.yeelink.net/v1.1/device/%s/sensor/%s/datapoints", device_id, sensor_id);
//        System.out.println("URL="+url);
//        Request req = Request.create(url, METHOD.POST);
//        req.setData(Json.toJson(data));
//        //req.getHeader().set("U-ApiKey", key);
//        Response resp = Sender.create(req).send();
//        System.out.println(resp.getStatus());
//    }
//    
//    @Test
//    public void test_12306() throws NoSuchAlgorithmException, KeyManagementException {
//        String url = "https://kyfw.12306.cn/otn/leftTicket/queryT?leftTicketDTO.train_date=2015-01-12&leftTicketDTO.from_station=UXP&leftTicketDTO.to_station=SJP&purpose_codes=ADULT";
//        try {
//            Http.disableJvmHttpsCheck();
//            Http.get(url);
//        }catch (Exception e) {
//            fail();
//        }
//    }
//    
//    @Test
//    public void test_360safe() throws Throwable {
//        Http.disableJvmHttpsCheck();
//        
//    	String url = "https://openapi.360.cn/user/me.json?access_token=1323463692b46eacce7412f3b65877cc54fc6d538db5619b20&fields=id,name,avatar,nick";
//
//        Response response = Http.get(url);
//        System.out.println(response.getContent());
//    }
    
//    @Test
//    public void test_cookie() {
//        Http.disableJvmHttpsCheck();
//        Cookie cookie = new Cookie();
//        cookie.setDebug(true);
//        Request req = Request.get("https://nutz.cn/user/login");
//        Response resp = Sender.create(req).setInterceptor(cookie).send();
//        String expected = cookie.toString();
//        System.out.println(resp.getStatus());
//        System.out.println(Json.toJson(cookie));
//        resp = Sender.create(req).setInterceptor(cookie).send();
//        System.out.println(Json.toJson(cookie)); // 第二次应该没有新的cookie
//        assertEquals(expected, cookie.toString()); // 所以两次toString的结果是一样的, 即同一个session
//    }
    
//    @Test
//    public void test_ys7() {
//        String re = Http.post("https://open.ys7.com/api/method", null, 5*1000);
//        assertNotNull(re);
//        System.out.println(re);
//    }
//    
//    @Test()
//    public void test_weibo_post() {
//        Response resp = Http.post2("http://weibo.com/kuyunhudong", null,10000);
//        System.out.println(resp.getStatus());
//        System.out.println(resp.getContent());
//        System.out.println(resp.getStatus());
//    }
}
