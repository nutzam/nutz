package org.nutz.mvc.upload;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.nutz.filepool.FilePool;

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
	 * @param charset
	 * @param tmps
	 * @throws UploadFailException
	 */
	Map<String, Object> parse(HttpServletRequest req, String charset, FilePool tmps)
			throws UploadFailException;
}