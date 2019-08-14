package org.nutz.lang.util;

import org.junit.Test;
import org.nutz.json.Json;
import org.nutz.json.JsonShape;
import org.nutz.lang.Mirror;

import junit.framework.Assert;

public class ResidentStatusTest {


    @JsonShape(JsonShape.Type.NAME)
    public enum ResidentStatus {

        Init(){
            @Override
            public void apply() {
                throw new RuntimeException("resident.apply.init");
            }
        },
        Confirm(){
            @Override
            public void apply() {
                throw new RuntimeException("resident.apply.exist");
            }

            @Override
            public void confirmRoommate() {
                throw new RuntimeException("resident.apply.confirm.conformed");

            }
        },
        Reject(){
            @Override
            public void confirmRoommate() {
                throw new RuntimeException("resident.apply.confirm.rejected");

            }
        };


        public void apply() {

        }

        public void confirmRoommate() {

        }
    }

    @Test
    public void test_IsEnum() throws NoSuchMethodException, SecurityException {

        ResidentStatus init = ResidentStatus.Init;

        Mirror<ResidentStatus> me = Mirror.me(init);

        System.out.println(init.getClass().isEnum());
        
        System.out.println(init.getClass());
        
        System.out.println(init.getDeclaringClass().isEnum());

        Assert.assertTrue(me.isEnum());
        
        System.out.println(init.getClass().getSuperclass());
        
        System.out.println(init.getDeclaringClass());
        
        System.out.println(init.getDeclaringClass().getMethod("name"));
        
        System.out.println(Json.toJson(new NutMap("e", init)));

    }
}
