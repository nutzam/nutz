package org.nutz.json;

import org.hsqldb.lib.StopWatch;
import org.junit.Test;
import org.nutz.json.bean.JsonObject;

import com.alibaba.fastjson.JSON;

public class JsonFaster {

    @Test
    public void json() {
        nutzJson(10000);
        fastJson(10000);
        
        StopWatch sw = new StopWatch();
        sw.start();
        nutzJson(50*10000);
        System.out.println(sw.elapsedTimeToMessage("Nutz-Json 50w次耗时: "));
        
        sw.start();
        fastJson(50*10000);
        System.out.println(sw.elapsedTimeToMessage("Fast-Json 50w次耗时: "));
        
        //-------------------------------------------------------------------
        sw.start();
        nutzJson(50*10000);
        System.out.println(sw.elapsedTimeToMessage("Nutz-Json 50w次耗时: "));
        
        sw.start();
        fastJson(50*10000);
        System.out.println(sw.elapsedTimeToMessage("Fast-Json 50w次耗时: "));
    }
    
    public void nutzJson(int time) {
        JsonObject obj = new JsonObject();
        obj.setName("wendal");
        for (int i = 0; i < time; i++) {
            String jsonStr = Json.toJson(obj);
            obj = Json.fromJson(JsonObject.class, jsonStr);
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
    
    public static void main(String[] args) throws Throwable {
        Thread.sleep(60*1000);
        new JsonFaster().json();
    }
}
