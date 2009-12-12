package org.nutz.http.sender;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import org.nutz.http.HttpException;
import org.nutz.http.Request;
import org.nutz.http.Response;

public class FilePostSender extends PostSender {

	public static final String SEPARATOR = "\r\n";
	
	public FilePostSender(Request request) {
		super(request);
	}
	
	@Override
	public Response send() throws HttpException {
		try {
			String boundary = "---------------------------[Nutz]7d91571440efc";
			openConnection();
			setupRequestHeader();
			conn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
			setupDoInputOutputFlag();
			Map<String, ?> params = request.getParams();
			DataOutputStream outs = new DataOutputStream(conn.getOutputStream());		
			if (null != params && params.size() > 0) {
				for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
					outs.writeBytes("--" + boundary + SEPARATOR);
					String key = it.next();
					File f = new File(params.get(key).toString());
					if(f.exists()){
						outs.writeBytes("Content-Disposition:	form-data;	name=\""+ key + "\";	filename=\""+ params.get(key) +"\"\r\n");
						outs.writeBytes("Content-Type:   application/octet-stream\r\n\r\n");
				        InputStream is = new FileInputStream(f);  			        	
				        byte[] buffer = new byte[is.available()];
					    while(true){  
					    	synchronized(buffer){  
					    		int amountRead = is.read(buffer);  
					            if(amountRead == -1){  
					            	break;  
					            } 
					            outs.write(buffer,0,amountRead);
					            outs.writeBytes("\r\n");
					        }
					    }
				     is.close();

					}else{
						outs.writeBytes("content-disposition:	form-data;	name=\"" + key + "\"\r\n\r\n"); 
						outs.writeBytes(params.get(key) + "\r\n");
					}			
				}
				outs.writeBytes("--" + boundary + "--"+ SEPARATOR);
				outs.flush();
				outs.close();
			}		

			return createResponse(getResponseHeader());

		} catch (Exception e) {
			throw new HttpException(request.getUrl().toString(), e);
		}
	}


}
