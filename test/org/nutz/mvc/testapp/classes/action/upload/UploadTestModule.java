package org.nutz.mvc.testapp.classes.action.upload;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

@InjectName
@IocBean
//@At("/upload")
@Ok("raw")
public class UploadTestModule {

    @AdaptBy(type=UploadAdaptor.class,args={"~/tmp"})
    @At("/upload/*")
    public String test_upload(String type, @Param("file")TempFile file){
        return type + "&" + file.getFile().length();
    }
}
