package org.nutz.mvc.view;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.mvc.View;

/**
 * 返回特定的响应码<p/>
 * <b>注意,400或以上,会调用resp.sendError,而非resp.setStatus.这样做的原因是 errorPage的配置,只有resp.sendError会触发,且绝大多数情况下,只会配置400或以上</b>
 * @author MingMing
 *
 */
public class HttpStatusView implements View {

	public static final View HTTP_404 = new HttpStatusView(404);
	//public static final View HTTP_400 = new HttpStatusView(400);
	public static final View HTTP_500 = new HttpStatusView(500);
	public static final View HTTP_502 = new HttpStatusView(502);

    private int sc;

    public HttpStatusView(int sc) {
        this.sc = sc;
    }

    public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) {
        if (sc >= 400)
            try {
                resp.sendError(sc);
            } catch (IOException e) {
                throw Lang.wrapThrow(e);
            }
        else
            resp.setStatus(sc);
    }

}
