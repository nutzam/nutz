package org.nutz.json.generic;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;

import org.junit.Test;
import org.nutz.json.Json;
import org.nutz.lang.util.NutType;

public class GenericTest {
    @SuppressWarnings("unchecked")
    @Test
    public void test() throws SecurityException, NoSuchFieldException{
        String str = "jk";
        String mobil = "13123132321";
        String json = "{'loginName' :'user1','body' :{'mobile' :'"+mobil+"'}, 'str':'"+str+"'}";
        Type type = new NutType(JsonRequest.class, Employee.class, String.class);
        JsonRequest<Employee, String> request = (JsonRequest<Employee, String>) Json.fromJson(type, json);
        assertEquals(request.body.mobile, mobil);
        assertEquals(request.str, str);
    }
}
