package org.nutz.json.generic;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;

import org.junit.Test;
import org.nutz.dao.entity.Record;
import org.nutz.json.Json;
import org.nutz.lang.util.NutType;

public class GenericTest {
    @SuppressWarnings("unchecked")
    @Test
    public void test() throws SecurityException, NoSuchFieldException {
        String str = "jk";
        String mobil = "13123132321";
        String json = "{'loginName' :'user1','body' :{'mobile' :'"
                      + mobil
                      + "'}, 'str':'"
                      + str
                      + "'}";
        Type type = new NutType(JsonRequest2.class, Employee2.class, String.class);
        JsonRequest2<Employee2, String> request = (JsonRequest2<Employee2, String>) Json.fromJson(type,
                                                                                                  json);
        assertEquals(request.body.mobile, mobil);
        assertEquals(request.str, str);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test2() {
        String str = "jk";
        String mobil = "13123132321";
        String json = "{'loginName' :'user1','body' :{'mobile' :'"
                      + mobil
                      + "'}, 'str':{'mobile' :'"
                      + str
                      + "'}}";
        Type type = new NutType(JsonRequest2.class, Employee2.class, Employee2.class);
        JsonRequest2<Employee2, Employee2> request = ((JsonRequest2<Employee2, Employee2>) Json.fromJson(type,
                                                                                                         json));
        assertEquals(request.body.mobile, mobil);
        assertEquals(request.str.mobile, str);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void receiveJsonRequest() {
        Employee user = new Employee();
        user.setMobile("13123132321");

        JsonRequest<Employee> request = new JsonRequest<Employee>();
        request.setBody(user);

        request.setLoginName("user1");
        request.setPassword("1234");
        request.setVersion("1101");
        String json = Json.toJson(request);
        System.out.println(json);

        request = (JsonRequest<Employee>) Json.fromJson(new NutType(request.getClass(),
                                                                    Employee.class), json);
        System.out.println(request.getBody().getMobile());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void receiveJsonRequest4Record() {
        Record record = new Record();
        record.put("a", "a");
        record.put("b", 1);

        JsonRequest<Record> request = new JsonRequest<Record>();
        request.setBody(record);
        request.setLoginName("user1");
        request.setPassword("1234");
        request.setVersion("1101");
        request.setUserType(JsonRequest.USER_TYPE_EMPLOYEE);
        String json = Json.toJson(request);
        System.out.println(json);

        request = (JsonRequest<Record>) Json.fromJson(new NutType(request.getClass(),
                                                                  record.getClass()),
                                                      json);
        System.out.println(request.getBody().getString("a"));
        System.out.println(request.getBody().getInt("b"));
    }

}
