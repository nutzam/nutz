package com.zzh.ioc;

import com.zzh.ioc.Value.ArrayValue;
import com.zzh.lang.born.Borning;
import com.zzh.lang.born.BorningInvoker;
import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;
import com.zzh.lang.MirrorBorning;

public class DynamicBorning<T> implements Borning<T> {

	private ArrayValue value;
	private BorningInvoker<T> invoker;

	DynamicBorning(Mirror<T> mirror, Value.ArrayValue value) {
		this.value = value;
		MirrorBorning<T> borning = mirror.getBorning(this.value.getArray());
		invoker = borning.getBorningInvoker();
		invoker.clearArgs();
	}

	@Override
	public T born() {
		try {
			return invoker.born(value.makeArray());
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

}
