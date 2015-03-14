package org.nutz.mvc.view;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.JsonFormat;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;

/**
 * 将数据采用json方式输出的试图实现
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author mawn(ming300@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class UTF8JsonView implements View {

    private JsonFormat format;

    private Object data;

    public void setData(Object data) {
        this.data = data;
    }

    public UTF8JsonView(JsonFormat format) {
        this.format = format;
    }

    public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
            throws IOException {
        Mvcs.write(resp, null == obj ? data : obj, format);
    }

    public static final View NICE = new UTF8JsonView(JsonFormat.nice());
    public static final View COMPACT = new UTF8JsonView(JsonFormat.compact());
    public static final View FULL = new UTF8JsonView(JsonFormat.full());
    public static final View FORLOOK = new UTF8JsonView(JsonFormat.forLook());
}
