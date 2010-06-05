package org.nutz.ioc.val;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;
import org.nutz.lang.Lang;

/**
 * 通过JNDI查找相应的对象
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class JNDI_Value implements ValueProxy{
	
	private String jndiName;
	private Context cntxt;
	
	public JNDI_Value(String jndiName) {
		this.jndiName = jndiName;
	}

	public Object get(IocMaking ing) {
		try {
			if (cntxt == null)
				cntxt = new InitialContext();
			cntxt.lookup(jndiName);
			return null;
		}
		catch (NamingException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
