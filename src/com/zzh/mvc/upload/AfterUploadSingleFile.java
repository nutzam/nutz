package com.zzh.mvc.upload;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.lang.Lang;

public abstract class AfterUploadSingleFile extends AfterUpload {

	@Override
	protected Object execute(Map<String, Object> params, HttpServletRequest request,
			HttpServletResponse response) {
		for (Iterator<Object> it = params.values().iterator(); it.hasNext();) {
			Object uf = it.next();
			if (uf instanceof UploadedFile)
				return this.execute((UploadedFile) uf, request, response);
		}
		throw Lang.makeThrow("Not file be uploaded!");
	}

	protected abstract Object execute(UploadedFile upfile, HttpServletRequest request,
			HttpServletResponse response);

}
