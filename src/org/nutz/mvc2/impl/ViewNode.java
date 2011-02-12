package org.nutz.mvc2.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.View;
import org.nutz.mvc2.ActionChain;

/**
 * 负责渲染视图
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class ViewNode extends AbstractActionNode {
	
	private static final Log log = Logs.getLog(ViewNode.class);

	@Override
	public void filter(ActionChain chain) throws Throwable {
		HttpServletRequest req = getRequest(chain);
		HttpServletResponse resp = getResponse(chain);
		try {
			chain.doChain();
			Object re = chain.get(ActionFilters.returnValue);
			// 渲染 HTTP 输出流
			if (re instanceof View)
				((View) re).render(req, resp, null);
			else
				((View) chain.get(ActionFilters.viewOK)).render(getRequest(chain), getResponse(chain), re);
		} catch (Throwable e) {
			// 基本上， InvocationTargetException 一点意义也没有，需要拆包
			if (e instanceof InvocationTargetException && e.getCause() != null)
				e = e.getCause();

			// 在 Debug 模式下，输出这个错误信息到日志里有助于调试
			if (log.isDebugEnabled())
				log.debug(getExceptionMessage(e), e);

			try {
				((View) chain.get(ActionFilters.viewFail)).render(req, resp, e);
			}
			// 失败渲染流程也失败的话，则试图直接渲染一下失败信息
			catch (Throwable e1) {
				// 打印 Log
				if (log.isWarnEnabled())
					log.warn(getExceptionMessage(e1), e1);

				resp.reset();
				try {
					resp.getWriter().write(e1.getMessage());
					resp.flushBuffer();
				}
				// 仍然失败？ 没办法，抛出异常吧
				catch (IOException e2) {
					// 打印 Log
					if (log.isWarnEnabled())
						log.warn(getExceptionMessage(e2), e2);

					throw Lang.wrapThrow(e2);
				}
			}
		}
	}
	
	private static final String getExceptionMessage(Throwable e) {
		e = Lang.unwrapThrow(e);
		return Strings.isBlank(e.getMessage()) ? e.getClass().getSimpleName() : e.getMessage();
	}
}
