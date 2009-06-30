package org.nutz.json;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class CustomizedJsonTest {

	@Test
	public void test_trout_in_map() {
		Trout t = new Trout();
		t.setColor(Trout.COLOR.RED);
		t.setWeight(8.78f);
		Map<String,Trout> m = new HashMap<String,Trout>();
		m.put("t1", t);
		String exp = "{t1:\"Trout[RED](8.78)\"}";
		String json = Json.toJson(m,JsonFormat.compact().setQuoteName(false));
		assertEquals(exp,json);
		
	}

}
