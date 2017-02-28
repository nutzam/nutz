package org.nutz.mvc.testapp.classes.action.upload;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

/**
 * @since 1.r.55开始使用与servlet 3.0+一致的Part接口,原方法标记为弃用.
 */
@InjectName
@IocBean
//@At("/upload")
@Ok("raw")
@Fail("http:500")
public class UploadTestModule {

    @AdaptBy(type=UploadAdaptor.class,args={"~/tmp"})
    @At("/upload/*")
    public String test_upload(String type, @Param("file")TempFile file){
        return type + "&" + file.getSize();
    }
    
    @AdaptBy(type=UploadAdaptor.class,args={"~/tmp2"})
    @At("/upload/issue1220")
    public String test_upload_issue1220(@Param("file")TempFile[] files){
        return ""+files.length +"," + files[0].getSize() + "," + files[1].getSize();
    }
}
