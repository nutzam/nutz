package org.nutz.lang;

public interface Each<T> {

	void invoke(int i, T ele, int length) throws ExitLoop, LoopException;

}
