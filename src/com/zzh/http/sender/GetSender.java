package com.zzh.http.sender;

import com.zzh.http.HttpException;
import com.zzh.http.Request;
import com.zzh.http.Response;
import com.zzh.http.Sender;

public class GetSender extends Sender {

	public GetSender(Request request) {
		super(request);
	}

	@Override
	public Response send() throws HttpException {
		try {
			openConnection();
			setupRequestHeader();
			setupDoInputOutputFlag();
			return createResponse(getResponseHeader());

		} catch (Exception e) {
			throw new HttpException(request.getUrl().toString(), e);
		}
	}


}
