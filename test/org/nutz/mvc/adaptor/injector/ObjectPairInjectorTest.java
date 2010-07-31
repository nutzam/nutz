package org.nutz.mvc.adaptor.injector;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.lang.Lang;
import org.nutz.mock.Mock;
import org.nutz.mock.servlet.MockHttpServletRequest;

public class ObjectPairInjectorTest {

	public static ObjectPairInjector inj(Class<?> type) {
		return new ObjectPairInjector(null, type);
	}

	public static ObjectPairInjector inj() {
		return inj(MvcTestPojo.class);
	}

	/**
	 * 这个测试将检验在 HTTP 请求中，如果存在多个参数同名的情况，本注入器能否正确处理
	 */
	@Test
	public void test_duplicated_name_params() {
		// 准备数据
		MockHttpServletRequest req = Mock.servlet.request();
		req.setParameter("num", 23);
		req.setParameterValues("names", Lang.array("A", "B", "C"));

		// 执行
		MvcTestPojo pojo = (MvcTestPojo) inj().get(req, null, null);

		// 检测
		assertEquals(23, pojo.num);
		assertEquals(3, pojo.names.length);
		assertEquals("A", pojo.names[0]);
		assertEquals("B", pojo.names[1]);
		assertEquals("C", pojo.names[2]);
	}

}
