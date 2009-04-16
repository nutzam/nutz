package com.zzh.dao.callback;

public abstract class Callback<R, P> {

	private Context context;

	public Callback() {
		context = new Context();
	}

	public Context getContext() {
		return context;
	}

	public abstract R invoke(P arg) throws Exception;

}
