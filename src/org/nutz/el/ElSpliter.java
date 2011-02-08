package org.nutz.el;

import java.io.Reader;
import java.util.List;

/**
 * 将接受的文本流输入，并将其拆解成有意义的元素 
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface ElSpliter {

	List<ElSymbol> splite(Reader reader);
	
}
