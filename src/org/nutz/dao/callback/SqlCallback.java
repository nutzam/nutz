package org.nutz.dao.callback;

public abstract class SqlCallback<R, P> {

	private Context context;

	public SqlCallback() {
		context = new Context();
	}

	public Context getContext() {
		return context;
	}

	public abstract R invoke(P arg) throws Exception;

}
