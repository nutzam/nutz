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
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Streams;
import org.nutz.lang.random.R;

/**
 * 文件上传
 * @author wendal
 * @author Kerbores
 * @email kerbores@gmail.com
 */
public class FilePostSender extends PostSender {
    
    public static final String SEPARATOR = "\r\n";

    public FilePostSender(Request request) {
        super(request);
    }

    @Override
    public Response send() throws HttpException {
        try {
            final String boundary = "---------------------------[nutz]"+R.UU32();
            openConnection();
            setupRequestHeader();
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            setupDoInputOutputFlag();
            Map<String, Object> params = request.getParams();
            if (null != params && params.size() > 0) {
                final DataOutputStream outs = new DataOutputStream(conn.getOutputStream());
                for (Entry<String, ?> entry : params.entrySet()) {
                    outs.writeBytes("--" + boundary + SEPARATOR);
                    final String key = entry.getKey();
                    File[] fs = null;
                    if (entry.getValue() instanceof File)
                        fs = Lang.array((File) entry.getValue());
                    else if (entry.getValue() instanceof File[])
                        fs = (File[]) entry.getValue();
                    if (fs != null) {
                        Lang.each(fs, new Each<File>() {

                            @Override
                            public void invoke(int index, File f, int length) throws ExitLoop, ContinueLoop, LoopException {
                                try {
                                    if (f != null && f.exists()) {
                                        outs.writeBytes("Content-Disposition:    form-data;    name=\"" + key + "\";    filename=\"" + f.getName() + "\"\r\n");
                                        outs.writeBytes("Content-Type:   application/octet-stream\r\n\r\n");
                                        if (f.length() == 0)
                                            return;
                                        InputStream is = Streams.fileIn(f);
                                        byte[] buffer = new byte[8192];
                                        while (true) {
                                            int amountRead = is.read(buffer);
                                            if (amountRead == -1) {
                                                break;
                                            }
                                            outs.write(buffer, 0, amountRead);
                                        }
                                        outs.writeBytes("\r\n");
                                        Streams.safeClose(is);
                                    }
                                    outs.writeBytes("--" + boundary + SEPARATOR);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        outs.writeBytes("Content-Disposition:    form-data;    name=\"" + key + "\"\r\n\r\n");
                        outs.write((entry.getValue() + "\r\n").getBytes());
                    }
                }
                outs.writeBytes("--" + boundary + "--" + SEPARATOR);
                Streams.safeFlush(outs);
                Streams.safeClose(outs);
            }

            return createResponse(getResponseHeader());

        } catch (IOException e) {
            throw new HttpException(request.getUrl().toString(), e);
        }
    }

}