package org.nutz.el;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;

public class ElTest {

	@Test
	public void test_simple_invoke() {
		Context context = new Context();
		context.set("a", Lang.map("{x:10,y:50,txt:'Hello'}"));

		assertEquals(100, El.eval(context, "a.get('x')*10").getInteger().intValue());
		assertEquals(49, El.eval(context, "a.get('y')-1").getInteger().intValue());
		assertEquals("Hello-40", El.eval(context, "a.get('txt')+(a.get('x')-a.get('y'))")
									.getString());
		assertEquals("Hello", El.eval(context, "a.get('txt')").getString());
		assertEquals(3, El.eval(context, "a.size()").getInteger().intValue());
	}

	@Test
	public void test_simple_count() {
		assertEquals((3 + 2 * 5), El.eval("3+2*5").getInteger().intValue());
		assertEquals(((3 + 2) * 5), El.eval("(3+2)*5").getInteger().intValue());

		assertEquals(Float.valueOf(.3f + .2f * .5f), El.eval(".3+.2*.5").getFloat());
		assertEquals(Float.valueOf((.5f + .1f) * .9f), El.eval("(.5 + 0.1)*.9").getFloat());
	}

	@Test
	public void test_invoke() {
		Context context = new Context();
		List<String> list = new ArrayList<String>();
		context.set("b", list);
		assertEquals(0,El.eval(context,"b.size()").getInteger().intValue());
		list.add("");
		assertEquals(1,El.eval(context,"b.size()").getInteger().intValue());
	}

}
