package org.nutz.mvc.upload;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AfterUploadSingleFile extends AfterUpload {

	@Override
	protected Object execute(Map<String, Object> params, HttpServletRequest request,
			HttpServletResponse response) {
		if (null != params)
			for (Iterator<Object> it = params.values().iterator(); it.hasNext();) {
				Object uf = it.next();
				if (uf instanceof UploadedFile)
					return this.execute((UploadedFile) uf, request, response);
			}
		return this.execute((UploadedFile) null, request, response);
	}

	protected abstract Object execute(UploadedFile upfile, HttpServletRequest request,
			HttpServletResponse response);

}
