package org.nutz.mvc.upload;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class Servlet3Uploading implements Uploading {

    private static final Log log = Logs.get();
    
    public Map<String, Object> parse(HttpServletRequest req, UploadingContext context)
            throws UploadException, UploadOutOfSizeException, UploadUnsupportedFileNameException,
            UploadUnsupportedFileTypeException {
        NutMap params = Uploads.createParamsMap(req);
        if (log.isDebugEnabled())
            log.debugf("Params map created - %s params", params.size());
        //------------------------------------
        // 这里用到Servlet3的API
        try {
            Collection<Part> parts = req.getParts();
            if (parts == null || parts.isEmpty()) {
                if (log.isDebugEnabled())
                    log.debug("None file found!");
            }
            for (Part part : parts) {
                if (part.getSize() == 0 && context.isIgnoreNull())
                    continue;
                FieldMeta meta = new FieldMeta(part.getHeader("Content-Disposition"));
                if (context.getMaxFileSize() > 0 && part.getSize() > context.getMaxFileSize())
                    throw new UploadOutOfSizeException(meta);
                if (log.isDebugEnabled())
                    log.debugf("Upload Info: name=%s,content_type=%s", meta.getFileLocalName(),meta.getContentType());
                // 检查是否通过文件名过滤
                if (!context.isNameAccepted(meta.getFileLocalName())) {
                    throw new UploadUnsupportedFileNameException(meta);
                }
                // 检查是否通过文件类型过滤
                if (!context.isContentTypeAccepted(meta.getContentType())) {
                    throw new UploadUnsupportedFileTypeException(meta);
                }
                File tmp = context.getFilePool().createFile(meta.getFileExtension());
                Files.write(tmp, part.getInputStream());
                part.delete(); //删除之
                params.add(meta.getName(), new TempFile(meta, tmp));
            }
            //------------------------------------
            return params;
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
        catch (ServletException e) {
            throw Lang.wrapThrow(e);
        }
    }

}
