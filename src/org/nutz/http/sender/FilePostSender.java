package org.nutz.http.sender;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.http.HttpException;
import org.nutz.http.Request;
import org.nutz.http.Response;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Streams;
import org.nutz.lang.random.R;

public class FilePostSender extends PostSender {

	public static final String SEPARATOR = "\r\n";

	public FilePostSender(Request request) {
		super(request);
	}

	public static FilePostSender create(Request request) {
		return new FilePostSender(request);
	}

	@Override
	public Response send() throws HttpException {
		try {
			String boundary = "---------------------------[Nutz]aabbcc" + R.UU32();
			openConnection();
			setupRequestHeader();
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			setupDoInputOutputFlag();
			Map<String, Object> params = request.getParams();
			if (null != params && params.size() > 0) {
				export(params, conn.getOutputStream(), boundary, request.getEnc());
			}

			return createResponse(getResponseHeader());

		} catch (IOException e) {
			throw new HttpException(request.getUrl().toString(), e);
		}
	}
	
	public static void export(Map<String, Object> params, OutputStream out, String boundary, final String enc) throws IOException {
	    final DataOutputStream outs = new DataOutputStream(out);
        for (Entry<String, ?> entry : params.entrySet()) {
            outs.writeBytes("--" + boundary + SEPARATOR);
            final String key = entry.getKey();
            Object val = entry.getValue();
            if (val == null)
                val = "";
            Lang.each(val, new Each<Object>() {
                @Override
                public void invoke(int index, Object ele, int length) throws ExitLoop, ContinueLoop, LoopException {

                    File f = null;
                    if (ele instanceof File)
                        f = (File) ele;
                    try {
                        if (f != null && f.exists() && f.length() > 0) {
                            outs.writeBytes("Content-Disposition:    form-data;    name=\""
                                    + key
                                    + "\";    filename=\"");
                            outs.write(f.getName().getBytes(enc));
                            outs.writeBytes("\"" + SEPARATOR);
                            outs.writeBytes("Content-Type:   application/octet-stream"
                                    + SEPARATOR
                                    + SEPARATOR);
                            InputStream is = null;
                            try {
                                is = Streams.fileIn(f);
                                Streams.write(outs, is);
                                outs.writeBytes(SEPARATOR);
                            }
                            finally {
                                Streams.safeClose(is);
                            }
                        } else {
                            outs.writeBytes("Content-Disposition:    form-data;    name=\""
                                    + key
                                    + "\""
                                    + SEPARATOR
                                    + SEPARATOR);
                            outs.write(String.valueOf(ele).getBytes(enc));
                            outs.writeBytes(SEPARATOR);
                        }
                    }
                    catch (Exception e) {
                        throw Lang.wrapThrow(e);
                    }
                }
            });
        }
        outs.writeBytes("--" + boundary + "--" + SEPARATOR);
        Streams.safeFlush(outs);
        Streams.safeClose(outs);
	}
}