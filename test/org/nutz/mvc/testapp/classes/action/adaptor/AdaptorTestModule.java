package org.nutz.mvc.testapp.classes.action.adaptor;

import java.util.List;

import junit.framework.TestCase;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.ioc.annotation.InjectName;
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
@Fail("raw")
public class AdaptorTestModule extends BaseWebappTest {

    @At("/json/pet/array")
    @AdaptBy(type = JsonAdaptor.class)
    public String getJsonPetArray(@Param("pets") Pet[] pets) {
        return String.format("pets(%d) %s", pets.length, "array");
    }

    @At("/json/pet/list")
    @AdaptBy(type = JsonAdaptor.class)
    public String getJsonPetList(@Param("pets") List<Pet> lst) {
        StringBuilder sb = new StringBuilder();
        for(Pet pet : lst)
            sb.append(',').append(pet.getName());
        return String.format("pets(%d) %s", lst.size(), "list");
    }
    
    // 传入的id,会是一个非法的字符串!!
    @At({"/err/param", "/err/param/?"})
    @Fail("http:500")
    public void errParam(@Param("id") long id, AdaptorErrorContext errCtx) {
        TestCase.assertNotNull(errCtx);
        TestCase.assertNotNull(errCtx.getErrors()[0]);
    }
}
