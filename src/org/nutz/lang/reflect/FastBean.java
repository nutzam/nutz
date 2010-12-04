package org.nutz.lang.reflect;

import org.nutz.lang.InvokingException;

public abstract class FastBean {

	public Object newInstance() throws InvokingException{
		try{
			return _newInstance();
		}
		catch (InvokingException e) {
			throw e;
		}
		catch (Throwable e) {
			throw new InvokingException("Error when invoke getter", e);
		}
	}

	public void setter(Object obj, String fieldName, Object value) throws InvokingException {
		try{
			System.out.println(fieldName +" ---> " + fieldName.hashCode());
			_setter(obj, fieldName.hashCode(), value);
		}
		catch (InvokingException e) {
			throw e;
		}
		catch (Throwable e) {
			throw new InvokingException("Error when invoke getter", e);
		}
	}

	public Object getter(Object obj, String fieldName) throws InvokingException {
		try{
			System.out.println(fieldName +" ---> " + fieldName.hashCode());
			return _getter(obj, fieldName.hashCode());
		}
		catch (InvokingException e) {
			throw e;
		}
		catch (Throwable e) {
			throw new InvokingException("Error when invoke getter", e);
		}
	}
	
	protected Object _newInstance() throws Throwable {
		return null;
	}
	
	protected void _setter(Object obj, int fieldName_hashCode, Object value) throws Throwable {
		throw new InvokingException("No such setter!", (Throwable)null);
	}

	protected Object _getter(Object obj, int fieldName_hashCode) throws Throwable {
		throw new InvokingException("No such getter!", (Throwable)null);
	}
	
}
