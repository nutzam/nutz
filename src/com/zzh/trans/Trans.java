package com.zzh.trans;

import java.sql.SQLException;

import com.zzh.lang.Lang;

public abstract class Trans {

	private static ThreadLocal<Transaction> TN = new ThreadLocal<Transaction>();
	private static Class<? extends Transaction> implClass;

	public static Transaction get() {
		return TN.get();
	}

	public static void setup(Class<? extends Transaction> classOfTransaction) {
		implClass = classOfTransaction;
	}

	private static Transaction begain() {
		if (null != TN.get())
			return TN.get();
		// throw new
		// RuntimeException("Fail to create new trans because Transaction ["
		// + TN.get().getId() + "] still opening.");
		try {
			if (null == implClass)
				TN.set(new NutTransaction());
			else
				TN.set(implClass.newInstance());
			return TN.get();
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

	private static void commit() throws SQLException {
		if (null == TN.get())
			return;
		// @TODO it is not safe! should prepare Savepoint for multi datasouce
		// rollback together
		TN.get().commit();
		depose();
	}

	private static void depose() {
		TN.set(null);
	}

	private static void rollback() {
		if (null == TN.get())
			return;
		TN.get().rollback();
		depose();
	}

	public static void exec(Atom... atoms) {
		if (null == atoms)
			return;
		begain();
		try {
			for (Atom atom : atoms)
				atom.run();
			commit();
		} catch (Exception e) {
			rollback();
			throw Lang.wrapThrow(e);
		} finally {
			depose();
		}

	}
}
