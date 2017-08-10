package org.nutz.json;

import org.junit.Test;
import org.nutz.json.bean.JsonObject;
import org.nutz.lang.Stopwatch;

import com.alibaba.fastjson.JSON;

public class JsonFaster {

    @Test
    public void json() {
        String jsonStr = "{name:'wendal'}";
        nutzJson(100000, jsonStr);
        fastJson(100000, jsonStr);
        System.gc();
        
        int t = 5000*10000;
        
        Stopwatch sw = Stopwatch.begin();
        nutzJson(t, jsonStr);
        sw.stop();
        System.out.println("Nutz-Json "+t+"次耗时: " + sw.getDuration());
        System.gc();
        
        sw = Stopwatch.begin();
        //fastJson(t, jsonStr);
        sw.stop();
        System.out.println("Fast-Json "+t+"次耗时: " + sw.getDuration());
        System.gc();
        
        //-------------------------------------------------------------------
        sw = Stopwatch.begin();
        nutzJson(t, jsonStr);
        sw.stop();
        System.out.println("Nutz-Json "+t+"次耗时: " + sw.getDuration());
        System.gc();
        
        sw = Stopwatch.begin();
        //fastJson(t, jsonStr);
        sw.stop();
        System.out.println("Fast-Json "+t+"次耗时: " + sw.getDuration());
        System.gc();
    }
    
    public void nutzJson(int time) {
        JsonObject obj = new JsonObject();
        obj.setName("wendal");
        for (int i = 0; i < time; i++) {
            String jsonStr = Json.toJson(obj);
            Json.fromJson(jsonStr);
        }
    }
    
    public void fastJson(int time) {
        JsonObject obj = new JsonObject();
        obj.setName("wendal");
        for (int i = 0; i < time; i++) {
            String jsonStr = JSON.toJSONString(obj);
            obj = JSON.parseObject(jsonStr, JsonObject.class);
        }
    }
    
    public void nutzJson(int time, String jsonStr) {
        for (int i = 0; i < time; i++) {
            Json.fromJson(JsonObject.class, jsonStr);
        }
    }
    
    public void fastJson(int time, String jsonStr) {
        for (int i = 0; i < time; i++) {
            JSON.parseObject(jsonStr, JsonObject.class);
        }
    }
    
    public static void main(String[] args) throws Throwable {
        Thread.sleep(60*1000);
        new JsonFaster().json();
    }
}
