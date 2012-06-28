package org.nutz.mvc.init.module;

import java.lang.reflect.Type;
import java.util.List;

import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

@At("/simple")
@Ok("json")
@Fail("json")
public class SimpleTestModule {
    
    /**
     * For issue 417
     */
    @At
    public void testArrayArgs(@Param("names") List<String>[] names){
    }
    
    /**
     * For issue 417
     */
    @At
    public void testArrayArgs2(@Param("names") String[] names){
    }
    
    /**
     * For issue 417
     */
    @At
    public void testArrayArgs3(@Param("names") List<? extends Type>[] names){
    }
    
    /**
     * For issue 417
     */
    @At
    public void testArrayArgs4(@Param("names") List<? super Type>[] names){
    }

    /**
     * For issue 417
     */
    @At
    public void testArrayArgs5(@Param("names") List<?>[] names){
    }

}
