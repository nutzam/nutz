package org.nutz.mvc.testapp.classes.action.adaptor;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.lang.Streams;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.adaptor.meta.Pet;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.impl.AdaptorErrorContext;
import org.nutz.mvc.testapp.BaseWebappTest;

@InjectName
@IocBean
@At("/adaptor")
@Ok("raw")
@Fail("http:500")
public class AdaptorTestModule extends BaseWebappTest {

    /*
     * Githut : #352
     */
    @At("/reader")
    public String getInputStream(Reader reader) {
        return Streams.readAndClose(reader);
    }

    /*
     * Githut : #352
     */
    @At("/ins")
    public String getInputStream(InputStream ins) {
        return new String(Streams.readBytesAndClose(ins));
    }

    @At("/json/pet/array")
    @AdaptBy(type = JsonAdaptor.class)
    public String getJsonPetArray(@Param("pets") Pet[] pets) {
        return String.format("pets(%d) %s", pets.length, "array");
    }

    @At("/json/pet/list")
    @AdaptBy(type = JsonAdaptor.class)
    public String getJsonPetList(@Param("pets") List<Pet> lst) {
        StringBuilder sb = new StringBuilder();
        for (Pet pet : lst)
            sb.append(',').append(pet.getName());
        return String.format("pets(%d) %s", lst.size(), "list");
    }

    // 传入的id,会是一个非法的字符串!!
    @At({"/err/param", "/err/param/?"})
    public void errParam(@Param("id") long id, AdaptorErrorContext errCtx) {
        TestCase.assertNotNull(errCtx);
        TestCase.assertNotNull(errCtx.getErrors()[0]);
    }

    @At("/json/type")
    @AdaptBy(type = JsonAdaptor.class)
    public void jsonMapType(Map<String, Double> map) {
        TestCase.assertNotNull(map);
        TestCase.assertEquals(1, map.size());
        TestCase.assertEquals(123456.0, map.get("abc").doubleValue());
        System.out.println(map.get("abc"));
    }
}
