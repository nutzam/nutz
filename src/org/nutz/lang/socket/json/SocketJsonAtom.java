package org.nutz.lang.socket.json;

import java.io.IOException;
import java.io.Writer;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.json.JsonException;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.socket.SocketAction;
import org.nutz.lang.socket.SocketActionTable;
import org.nutz.lang.socket.SocketAtom;
import org.nutz.lang.socket.SocketContext;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class SocketJsonAtom extends SocketAtom {


    private static final Log log = Logs.get();

    public SocketJsonAtom(Context context, Socket socket,
            SocketActionTable saTable) {
        super(context, socket, saTable);
    }
    @SuppressWarnings("unchecked")
    public void doRun() throws IOException {
        StringBuilder sb = new StringBuilder();
        // 预先读取一行
        line = br.readLine();
        // 在这个 socket 中逐行读取 ...
        while (null != line) {
            if (context.getBoolean("stop"))
                return;
            
            sb.append(line).append('\n');
            // 前面有空行
            if (Strings.isBlank(line))
                break;
            // 接着读 ...
            line = br.readLine();
        }
        // 打印信息
        if (log.isDebugEnabled())
            log.debug("  <<socket<<: " + sb);

        // 解析成 JSON
        try {
            LinkedHashMap<String, Object> map = Json.fromJson(LinkedHashMap.class, sb.toString());
            if (map == null) {
                if (log.isWarnEnabled())
                    log.warn("Null data ???!!");
                return;
            }
            SocketAction action = saTable.get(map.get("cmd").toString());
            if (null != action) {
                if (log.isDebugEnabled())
                    log.debugf("handle request by "+ action);
                SocketContext context = new SocketContext(this);
                context.set("json_data", map);
                action.run(context);
                if (log.isDebugEnabled())
                    log.debugf("finish request by "+ action);
            } else {
                if (log.isWarnEnabled())
                    log.warn("Unknown CMD="+map.get("cmd"));
                Writer writer = Streams.utf8w(ops);
                Map<String, Object> x = new HashMap<String, Object>();
                x.put("ok", false);
                x.put("msg", "Unknown CMD");
                Json.toJson(writer, x);
                try {
                    writer.close();
                }
                catch (IOException e) {
                    if (log.isWarnEnabled())
                        log.warn("Error to write...", e);
                }
            }
        }
        catch (JsonException e) {
            if (log.isWarnEnabled())
                log.warnf("Json error > %s : \n<%s>", e.getMessage(), sb);
        }
    }
}
