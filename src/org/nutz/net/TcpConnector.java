package org.nutz.net;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.BufferedReader;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 通过 TCP 与远端服务保持一个长期的连接
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class TcpConnector {

    private static final Log log = Logs.get();

    private Socket socket;

    private BufferedReader reader;

    private Writer writer;

    public TcpConnector connect() {
        if (isClosed()) {
            log.infof("Connect socket <-> %s:%d", host, port);
            try {
                socket = new Socket(InetAddress.getByName(host), port);
                socket.setTcpNoDelay(true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new OutputStreamWriter(socket.getOutputStream());
            }
            catch (UnknownHostException e) {
                log.warnf("Unknown host '%s:%d'", host, port);
                throw Lang.wrapThrow(e);
            }
            catch (IOException e) {
                log.warnf("IOError '%s:%d'", host, port);
                throw Lang.wrapThrow(e);
            }
        }
        return this;
    }

    public String readLine() throws IOException {
        return reader.readLine();
    }

    public TcpConnector write(String str) throws IOException {
        if (isClosed()) {
            connect();
        }
        writer.write(str);
        writer.flush();
        return this;
    }

    public TcpConnector writeLine(String str) throws IOException {
        return write(str + "\n");
    }

    public boolean isClosed() {
        return null == socket;
    }

    public TcpConnector close() {
        if (null != socket)
            try {
                log.infof("Close socket <-> %s:%d", host, port);
                socket.close();
            }
            catch (IOException e) {
                log.warn("fail to close", e);
            }
        socket = null;
        reader = null;
        writer = null;

        return this;
    }

    public TcpConnector(String host, int port) {
        this.host = host;
        this.port = port;
        this.socket = null;
    }

    private String host;

    private int port;

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

}
