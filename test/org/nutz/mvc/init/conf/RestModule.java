package org.nutz.mvc.init.conf;

import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.DELETE;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.OPTIONS;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.PATCH;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.PUT;

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

    @At("/abc")
    @OPTIONS
    public String options() {
        return "options";
    }

    @At("/abc")
    @PATCH
    public String patch() {
        return "patch";
    }

    @At("/oag")
    @GET
    @OPTIONS
    public String optionsAndGet() {
        return "options&get";
    }

    @At("/oap")
    @POST
    @OPTIONS
    public String optionsAndPost() {
        return "options&post";
    }
}
