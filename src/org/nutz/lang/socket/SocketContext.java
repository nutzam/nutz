package org.nutz.lang.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
import org.nutz.lang.util.SimpleContext;

public class SocketContext extends SimpleContext {

    private SocketAtom atom;

    public SocketContext(SocketAtom atom) {
        this.atom = atom;
    }

    public BufferedReader getReader() {
        return atom.br;
    }

    public String readLine() throws IOException {
        if (atom.socket.isClosed())
            return null;
        
        return atom.br.readLine();
    }

    public String getCurrentLine() {
        return atom.line;
    }

    public OutputStream getOutputStream() {
        return atom.ops;
    }

    public void write(String str) {
        if (!atom.socket.isClosed())
            try {
                atom.ops.write(str.getBytes(Encoding.UTF8));
            }
            catch (IOException e) {
                throw Lang.wrapThrow(e);
            }
    }

    public void writeLine(String str) {
        write(str + "\n");
    }

}
