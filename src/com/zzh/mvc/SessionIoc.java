package com.zzh.mvc;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.zzh.castor.Castors;
import com.zzh.ioc.FailToMakeObjectException;
import com.zzh.ioc.MappingLoader;
import com.zzh.ioc.Nut;
import com.zzh.ioc.ObjectNotFoundException;

public class SessionIoc extends Nut {

	private HttpSession session;

	public SessionIoc(HttpSession session, MappingLoader loader) {
		super(loader);
		attachSession(session);
	}

	public void attachSession(HttpSession session) {
		this.session = session;
	}

	@Override
	public void clear() {
		session = null;
		super.clear();
	}

	@Override
	public boolean isSingleton(Class<?> classOfT, String name) {
		if (session.getAttribute(name) != null)
			return true;
		return super.isSingleton(classOfT, name);
	}

	@Override
	public <T> T get(Class<T> classOfT, String name) throws FailToMakeObjectException,
			ObjectNotFoundException {
		Object obj = session.getAttribute(name);
		if (null != obj)
			return Castors.me().castTo(obj, classOfT);
		return super.get(classOfT, name);
	}

	@Override
	public String[] keys() {
		String[] superKeys = super.keys();
		Enumeration<?> ems = session.getAttributeNames();
		List<String> keys = new LinkedList<String>();
		while (ems.hasMoreElements()) {
			Object next = ems.nextElement();
			keys.add(ems.nextElement() == null ? null : next.toString());
		}
		String[] re = new String[keys.size() + superKeys.length];
		int i = 0;
		for (Iterator<String> it = keys.iterator(); it.hasNext();)
			re[i++] = it.next();
		for (String key : superKeys)
			re[i++] = key;
		return re;
	}

}
