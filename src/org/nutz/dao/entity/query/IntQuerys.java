package org.nutz.dao.entity.query;

import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.entity.EntityName;
import org.nutz.dao.entity.IntQuery;
import org.nutz.lang.segment.CharSegment;

import static java.lang.String.*;

public class IntQuerys {

	private static final String MAXSQL = "SELECT MAX(%s) FROM %s";

	private static CharSegment seg(EntityName enn, String colnm) {
		return new CharSegment(format(MAXSQL, colnm, enn.getOrignalString()));
	}

	/**
	 * Here we will make a IntQuery object base one the parameters.
	 * <p>
	 * we will pick the SQL pattern from '@strs' upon the '@meta''s name
	 * 
	 * @param meta
	 *            : Indicate the database product type
	 * @param enn
	 *            : entity view name
	 * @param strs
	 *            : user configuration array
	 * @param colnm
	 *            : the field columdn name
	 * @return IntQuery Object
	 */
	public static IntQuery create(DatabaseMeta meta, EntityName enn, String[] strs, String colnm) {
		// Default : SELECT MAX(@Id) FROM @View
		if (strs.length == 0) {
			// @View name is dynamice
			if (enn instanceof EntityName.DynamicEntityName)
				return new DynamicIntQuery(seg(enn, colnm));
			// @View is statice
			return new StaticIntQuery(format(MAXSQL, colnm, enn.value()));
		}
		// User input @XXX({"...sql..."})
		else if (strs.length == 1) {
			CharSegment seg = new CharSegment(strs[0]);
			if (seg.keys().size() == 0)
				return new StaticIntQuery(strs[0]);
			return new DynamicIntQuery(seg);
		}
		// User input @XXX({"psql", "...sql...", "mysql", "...sql..."})
		else if (strs.length % 2 == 0) {
			for (int i = 0; i < strs.length; i++)
				if (i % 2 == 0 && meta.is(strs[i])) {
					CharSegment seg = new CharSegment(strs[i + 1]);
					if (seg.keys().size() == 0)
						return new StaticIntQuery(strs[i + 1]);
					return new DynamicIntQuery(seg);
				}
		} else
			throw new RuntimeException(format("field [%s] fail to create IntQuery Object!", colnm));

		// Default return
		return null;
	}

	public static IntQuery serial(DatabaseMeta meta, EntityName enn, String[] strs, String colnm) {
		IntQuery iq = IntQuerys.create(meta, enn, strs, colnm);
		if (null == iq)
			return new StaticIntQuery(format(MAXSQL, colnm, enn.value()));
		return iq;
	}
}
