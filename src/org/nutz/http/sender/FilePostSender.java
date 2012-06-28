package org.nutz.http.sender;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.http.HttpException;
import org.nutz.http.Request;
import org.nutz.http.Response;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;

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
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            setupDoInputOutputFlag();
            Map<String, Object> params = request.getParams();
            if (null != params && params.size() > 0) {
                DataOutputStream outs = new DataOutputStream(conn.getOutputStream());
                for (Entry<String,?> entry : params.entrySet()) {
                    outs.writeBytes("--" + boundary + SEPARATOR);
                    String key = entry.getKey();
                    File f = null;
                    if (entry.getValue() instanceof File)
                        f = (File)entry.getValue();
                    else if (entry.getValue() instanceof String)
                        f = Files.findFile(entry.getValue().toString());
                    if (f != null && f.exists()) {
                        outs.writeBytes("Content-Disposition:    form-data;    name=\""
                                        + key
                                        + "\";    filename=\""
                                        + entry.getValue()
                                        + "\"\r\n");
                        outs.writeBytes("Content-Type:   application/octet-stream\r\n\r\n");
                        if(f.length() == 0)
                            continue;
                        InputStream is = Streams.fileIn(f);
                        byte[] buffer = new byte[is.available()];
                        while (true) {
                            int amountRead = is.read(buffer);
                            if (amountRead == -1) {
                                break;
                            }
                            outs.write(buffer, 0, amountRead);
                            outs.writeBytes("\r\n");
                        }
                        Streams.safeClose(is);
                    } else {
                        outs.writeBytes("Content-Disposition:    form-data;    name=\""
                                        + key
                                        + "\"\r\n\r\n");
                        outs.writeBytes(entry.getValue() + "\r\n");
                    }
                }
                outs.writeBytes("--" + boundary + "--" + SEPARATOR);
                Streams.safeFlush(outs);
                Streams.safeClose(outs);
            }

            return createResponse(getResponseHeader());

        }
        catch(IOException e) {
            throw new HttpException(request.getUrl().toString(), e);
        }
    }
}
