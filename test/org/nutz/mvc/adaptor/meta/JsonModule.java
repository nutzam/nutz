package org.nutz.mvc.adaptor.meta;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.nutz.lang.util.NutMap;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

@AdaptBy(type = JsonAdaptor.class)
@At("/json")
@Ok("json")
@Fail("json")
public class JsonModule {

    @At("/hello")
    public String hello(@Param("pet") Pet pet) throws UnsupportedEncodingException {
        return "!!" + pet.getName() + "!!";
    }

    @At("/map")
    public int jsonMap(NutMap map) {
        return map.size();
    }

    @At("/list")
    public int jsonList(List<Pet> pets) {
        return pets.size();
    }

    @At("/array")
    public int jsonArray(Pet[] pets) {
        return pets.length;
    }

    @At("/map/obj")
    public int mapPet(Pet pet) {
        return pet.map.size();
    }

}
