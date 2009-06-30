package org.nutz.mvc.access;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.nutz.NUT;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.ObjLoader;
import org.nutz.mvc.IocMvc;
import org.nutz.mvc.MvcSupport;
import org.nutz.mvc.SessionCallback;
import org.nutz.mvc.SessionIoc;

public class Session {

	public static Session me(HttpServletRequest request) {
		return me(request.getSession());
	}

	public static Session me(HttpSession session) {
		Session s = (Session) session.getAttribute(Session.class.getName());
		if (null == s) {
			synchronized (session) {
				s = (Session) session.getAttribute(Session.class.getName());
				if (null == s) {
					s = new Session(session);
					session.setAttribute(Session.class.getName(), s);
				}
			}
		}
		return s;
	}

	private HttpSession session;

	private Session(HttpSession session) {
		this.session = session;
		processCallback(NUT.WHEN_SESSION_START);
	}

	private void processCallback(String name) {
		Object callback = this.session.getServletContext().getAttribute(name);
		if (callback instanceof SessionCallback)
			((SessionCallback) callback).process(this.session);
	}

	@SuppressWarnings("unchecked")
	public <T extends Account> T getAccount(Class<T> type) {
		return (T) session.getAttribute(type.getName());
	}

	public <T extends Account> void setAccount(T account) {
		session.setAttribute(account.getClass().getName(), account);
	}

	public <T extends Account> T removeAccount(Class<T> type) {
		T re = getAccount(type);
		session.removeAttribute(type.getName());
		return re;
	}

	public <T extends Account> boolean hasAccount(Class<T> type) {
		return null != session.getAttribute(type.getName());
	}

	public <C> C getObject(Class<C> type) {
		return getObject(type, type.getName());
	}

	@SuppressWarnings("unchecked")
	public <C> C getObject(Class<C> type, String name) {
		return (C) session.getAttribute(name);
	}

	public void setObject(Object obj) {
		session.setAttribute(obj.getClass().getName(), obj);
	}

	public void setObject(Class<?> type, Object obj) {
		setObject(type.getName(), obj);
	}

	public void setObject(String name, Object obj) {
		session.setAttribute(name, obj);
	}

	public <C> C removeObject(Class<C> type) {
		C re = getObject(type);
		session.removeAttribute(type.getName());
		return re;
	}

	public void removeObject(String name) {
		session.removeAttribute(name);
	}

	public Ioc ioc() {
		Ioc ioc = getObject(Ioc.class);
		if (null == ioc) {
			synchronized (session) {
				ioc = getObject(Ioc.class);
				if (null == ioc) {
					Object obj = session.getServletContext().getAttribute(Ioc.class.getName());
					if (obj instanceof Ioc) {
						ioc = (Ioc) obj;
					} else if (obj instanceof ObjLoader) {
						ioc = new SessionIoc(session, (ObjLoader) obj);
					}
					setObject(Ioc.class, ioc);
				}
			}
		}
		return ioc;
	}

	public MvcSupport mvc() {
		MvcSupport mvc = getObject(MvcSupport.class);
		if (null == mvc) {
			mvc = getObject(MvcSupport.class);
			if (null == mvc) {
				mvc = new IocMvc(ioc(), (ServletConfig) session.getServletContext().getAttribute(
						ServletConfig.class.getName()));
				setObject(MvcSupport.class, mvc);
			}
		}
		return mvc;
	}

	public void detach() {
		Object obj = session.getAttribute(Ioc.class.getName());
		if(obj instanceof SessionIoc)
			((SessionIoc)obj).depose();
		removeObject(Ioc.class);
		removeObject(MvcSupport.class);
		removeObject(Session.class);
		processCallback(NUT.WHEN_SESSION_END);
	}

}
