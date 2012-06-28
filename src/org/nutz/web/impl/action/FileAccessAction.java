package org.nutz.web.impl.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.web.NutHttpAction;
import org.nutz.web.NutHttpReq;
import org.nutz.web.NutHttpResp;
import org.nutz.web.tools.Mimes;

public class FileAccessAction implements NutHttpAction {
    
    private static final Log log = Logs.get();
    
    private String root;
    
    public FileAccessAction(String root) {
        this.root = root;
    }

    public void exec(NutHttpReq req, NutHttpResp resp) {
        try {
            File f = new File(root + req.requestURI());
            if (f.exists() && f.isDirectory()) {
                f = new File(root + req.requestURI() + "/index.html");
            }
            log.debugf("File=%s, exist=%s", f.getPath(), f.exists());
            if (f.exists() && f.isFile()) {
                resp.setContentLength((int)f.length());
                resp.setContentType(Mimes.guess(Files.getSuffixName(f)));
                resp.headers().setDate("Last-Modify", f.lastModified());
                resp.sendRespHeaders();
                Streams.write(resp.getOutputStream(), new FileInputStream(f));
            } else {
                log.debug("File not found , send 404");
                resp.sendError(404, "File Not Found", null);
            }
        } catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }
    
    public boolean canWork(NutHttpReq req) {
        return "GET".equalsIgnoreCase(req.getMethod());
    }

}
