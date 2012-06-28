package org.nutz.mvc.upload;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 封装了上传的读取逻辑
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface Uploading {

    /**
     * 对流的解析
     * 
     * @param req
     * @param context
     * 
     * @throws UploadException
     * @throws UploadOutOfSizeException
     * @throws UploadUnsupportedFileNameException
     * @throws UploadUnsupportedFileTypeException
     */
    Map<String, Object> parse(HttpServletRequest req, UploadingContext context)
            throws UploadException, UploadOutOfSizeException, UploadUnsupportedFileNameException,
            UploadUnsupportedFileTypeException;
}