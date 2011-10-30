package org.nutz.json.generic;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import org.junit.Test;
import org.nutz.json.Json;
import org.nutz.lang.util.NutType;

import static org.junit.Assert.*;

public class GenericTest {
    @Test
    public void test() throws SecurityException, NoSuchFieldException{
        String mobil = "13123132321";
        String json = "{'loginName' :'user1','body' :{'mobile' :'"+mobil+"'}}";
        Type type = new NutType(JsonRequest.class, Employee.class);
        JsonRequest<Employee> jr = new JsonRequest<Employee>();
        Field fi = jr.getClass().getField("body");
        Type ty = fi.getGenericType();
        TypeVariable<?>[] tvs = jr.getClass().getTypeParameters();
        
        JsonRequest<Employee> request = (JsonRequest<Employee>) Json.fromJson(type, json);
        assertEquals(request.body.mobile, mobil);
    }
}
