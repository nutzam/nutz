package org.nutz.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

import org.nutz.lang.Strings;

public abstract class SocketLineHandler implements SocketHandler {

    public void handle(Socket socket) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Writer bw = new OutputStreamWriter(socket.getOutputStream());

        String line;

        while ((line = br.readLine()) != null) {
            String re = handleLine(line);
            if (!Strings.isBlank(re)) {
                bw.write(re);
                bw.flush();
            }
        }

    }

    protected abstract String handleLine(String line);

}
