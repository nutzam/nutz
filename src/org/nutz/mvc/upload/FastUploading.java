package org.nutz.mvc.upload;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.nutz.filepool.FilePool;
import org.nutz.http.Http;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.upload.util.BufferRing;
import org.nutz.mvc.upload.util.MarkMode;
import org.nutz.mvc.upload.util.RemountBytes;

/**
 * 采用成块写入的方式，这个逻辑比 SimpleUploading 大约快了 1 倍
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class FastUploading implements Uploading {

    private static final Log log = Logs.get();

    public Map<String, Object> parse(HttpServletRequest req, UploadingContext context)
            throws UploadException {
        if (log.isDebugEnabled())
            log.debug("FastUpload : " + Mvcs.getRequestPath(req));
            
        /*
         * 初始化一些临时变量
         */
        int bufferSize = context.getBufferSize();
        String charset = context.getCharset();
        FilePool tmps = context.getFilePool();
        int maxFileSize = context.getMaxFileSize();

        /*
         * 创建进度对象
         */
        UploadInfo info = Uploads.createInfo(req);
        if (log.isDebugEnabled())
            log.debug("info created");
        /*
         * 创建参数表
         */
        NutMap params = Uploads.createParamsMap(req);
        if (log.isDebugEnabled())
            log.debugf("Params map created - %s params", params.size());
        /*
         * 解析边界
         */
        String firstBoundary = "--" + Http.multipart.getBoundary(req.getContentType());
        RemountBytes firstBoundaryBytes = RemountBytes.create(firstBoundary);
        String itemEndl = "\r\n--" + Http.multipart.getBoundary(req.getContentType());
        RemountBytes itemEndlBytes = RemountBytes.create(itemEndl);
        RemountBytes nameEndlBytes = RemountBytes.create("\r\n\r\n");

        if (Http.multipart.getBoundary(req.getContentType()) == null) {
            if (log.isInfoEnabled())
                log.info("boundary no found!!");
            return params;
        }

        if (log.isDebugEnabled())
            log.debug("boundary: " + itemEndl);

        /*
         * 准备缓冲环，并跳过开始标记
         */
        MarkMode mm;
        BufferRing br;
        try {
            ServletInputStream ins = req.getInputStream();
            // 构建 3 个环节点的缓冲环
            br = new BufferRing(ins, 3, bufferSize);
            // 初始加载
            info.current = br.load();
            // 跳过开始的标记
            mm = br.mark(firstBoundaryBytes);
            // 这是不可能的，应该立即退出
            if (mm != MarkMode.FOUND) {
                if (log.isWarnEnabled())
                    log.warnf("Fail to find the firstBoundary (%s) in stream, quit!", firstBoundary);
                return params;
            }
            br.skipMark();
            if (log.isDebugEnabled())
                log.debug("skip first boundary");
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }

        /**
         * ========================================================<br>
         * 进入循环
         */
        if (log.isDebugEnabled())
            log.debug("Reading...");
        try {
            FieldMeta meta;
            do {
                info.current = br.load();
                // 标记项目头
                mm = br.mark(nameEndlBytes);
                String s = br.dumpAsString(charset);

                // 肯定碰到了 "--\r\n"， 这标志着整个流结束了
                if ("--".equals(s) || MarkMode.STREAM_END == mm) {
                    break;
                }
                // 找到头的结束标志
                else if (MarkMode.FOUND == mm) {
                    meta = new FieldMeta(s);
                }
                // 这是不可能的，抛错
                else {
                    throw new UploadInvalidFormatException("Fail to found nameEnd!");
                }
                if(log.isDebugEnabled())
                    log.debugf("Upload File info: FilePath=[%s],fieldName=[%s]",meta.getFileLocalPath(),meta.getName());
                // 作为文件读取
                if (meta.isFile()) {
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

                    // 上传的是一个空文件
                    if ("\"\"".equals(meta.getName()) || Strings.isBlank(meta.getFileLocalPath())) {
                        do {
                            info.current = br.load();
                            mm = br.mark(itemEndlBytes);
                            assertStreamNotEnd(mm);
                            br.skipMark();
                        } while (mm == MarkMode.NOT_FOUND);
                    }
                    // 保存临时文件
                    else {
                        File tmp = tmps.createFile(meta.getFileExtension());
                        OutputStream ops = null;
                        try {
                            ops = new BufferedOutputStream(    new FileOutputStream(tmp),
                                                            bufferSize * 2);
                            // 需要限制文件大小
                            if (maxFileSize > 0) {
                                long maxPos = info.current + maxFileSize;
                                do {
                                    info.current = br.load();
                                    mm = br.mark(itemEndlBytes);
                                    assertStreamNotEnd(mm);
                                    if (info.current > maxPos) {
                                        throw new UploadOutOfSizeException(meta);
                                    }
                                    br.dump(ops);
                                    if(info.stop)
                                        throw new UploadStopException(info);
                                } while (mm == MarkMode.NOT_FOUND);
                            }
                            // 不限制文件大小
                            else {
                                do {
                                    info.current = br.load();
                                    mm = br.mark(itemEndlBytes);
                                    assertStreamNotEnd(mm);
                                    br.dump(ops);
                                    if(info.stop)
                                        throw new UploadStopException(info);
                                } while (mm == MarkMode.NOT_FOUND);
                            }
                        }
                        finally {
                            Streams.safeFlush(ops);
                            Streams.safeClose(ops);
                        }
                        // 如果是空文件，不保存
                        if (context.isIgnoreNull() && tmp.length() == 0) {}
                        // 默认，空文件也保存
                        else {
                            params.add(meta.getName(), new TempFile(meta, tmp));
                        }
                    }
                }
                // 作为提交值读取
                else {
                    StringBuilder sb = new StringBuilder();
                    do {
                        info.current = br.load();
                        mm = br.mark(itemEndlBytes);
                        assertStreamNotEnd(mm);
                        sb.append(br.dumpAsString(charset));
                    } while (mm == MarkMode.NOT_FOUND);
                    params.add(meta.getName(), sb.toString());
                    if (log.isDebugEnabled())
                        log.debugf(    "Found a param, name=[%s] value=[%s]",
                                    meta.getName(),
                                    sb.toString());
                }

            } while (mm != MarkMode.STREAM_END);
        }
        // 处理异常
        catch (IOException e) {
            throw Lang.wrapThrow(e, UploadException.class);
        }
        // 安全关闭输入流
        finally {
            br.close();
        }
        if (log.isDebugEnabled())
            log.debugf("...Done %s bytes readed", br.readed());
        /**
         * 全部结束<br>
         * ========================================================
         */

        return params;
    }

    private static void assertStreamNotEnd(MarkMode mm) throws UploadInvalidFormatException {
        if (mm == MarkMode.STREAM_END)
            throw new UploadInvalidFormatException("Should not end stream");
    }
}
