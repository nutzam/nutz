package org.nutz.mvc.testapp.upload;

import java.io.File;
import java.io.FileWriter;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.sender.FilePostSender;
import org.nutz.mvc.testapp.BaseWebappTest;

public class UploadTest extends BaseWebappTest {

    @Test
    public void test_upload() throws Throwable {
        Request req = Request.create(getBaseURL()+"/upload/image",METHOD.POST);
        File f = File.createTempFile("nutz", "data");
        FileWriter fw = new FileWriter(f);
        fw.write("abc");
        fw.flush();
        fw.close();
        req.getParams().put("file", f);
        FilePostSender sender = new FilePostSender(req);
        Response resp = sender.send();
        assertEquals("image&3", resp.getContent());
    }
    
}
