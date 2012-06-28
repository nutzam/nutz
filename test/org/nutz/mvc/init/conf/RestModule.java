package org.nutz.mvc.init.conf;

import org.nutz.mvc.annotation.*;

@Ok("raw")
@Fail("json")
public class RestModule {

    @At("/abc")
    @GET
    public String get() {
        return "get";
    }

    @At("/abc")
    @PUT
    public String put() {
        return "put";
    }

    @At("/abc")
    @POST
    public String post() {
        return "post";
    }

    @At("/abc")
    @DELETE
    public String delete() {
        return "delete";
    }

    @At("/xyz")
    @GET
    @POST
    public String getAndPost() {
        return "get&post";
    }

    @At("/a/?/b/?/c/*")
    public String pathArgs_01(int a, int b, String c) {
        return c + "?a=" + a + "&b=" + b;
    }

}
