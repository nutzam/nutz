package org.nutz.dao.tools.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.dao.tools.DField;
import org.nutz.dao.tools.DTable;
import org.nutz.dao.tools.DTableParser;
import org.nutz.lang.Strings;

public class NutDTableParser implements DTableParser {

	public List<DTable> parse(String str) {
		if (null == str)
			return null;
		InnerParser ip = new InnerParser(str.toCharArray());
		return ip.process();
	}

	class InnerParser {

		private char[] cs;
		private int index;

		InnerParser(char[] cs) {
			this.cs = cs;
		}

		private boolean isNeedContinue(char c) {
			boolean isContinue = false;
			if (c == '#') {
				// find the \n and continue
				index++;
				for (; index < cs.length; index++)
					if (cs[index] == '\n')
						break;
				isContinue = true;
			} else if (c > 0 && c <= 0x20) {
				isContinue = true;
			}
			return isContinue;
		}

		List<DTable> process() {
			List<DTable> list = new LinkedList<DTable>();
			for (; index < cs.length; index++) {
				char c = cs[index];
				if (isNeedContinue(c))
					continue;
				// find the name, when encounter the {, move next, break
				StringBuilder sb = new StringBuilder();
				for (; index < cs.length; index++) {
					c = cs[index];
					if (c == '{')
						break;
					sb.append(c);
				}
				// prepare the table object
				DTable dt = new DTable();
				dt.setName(Strings.trim(sb));
				// parse each fields
				DField df;
				while (null != (df = nextField()))
					dt.addField(df);
				list.add(dt);
			}
			return list;
		}

		DField nextField() {
			if (cs[index] == '}')
				return null;
			StringBuilder sb = new StringBuilder();
			boolean isInBracket = false;
			for (index++; index < cs.length; index++) {
				char c = cs[index];
				if (isNeedContinue(c))
					continue;
				// Read chars to '}' or ','
				for (; index < cs.length; index++) {
					c = cs[index];
					if (c == '(')
						isInBracket = true;
					if (!isInBracket) {
						if (c == '}' || c == ',')
							return createFieldByString(Strings.trim(sb));
					} else if (')' == c)
						isInBracket = false;
					sb.append(c);
				}
			}
			return null;
		}

		DField createFieldByString(String str) {
			if (str.length() == 0)
				return null;
			DField field = new DField();
			char[] cs = str.toCharArray();
			// Set name
			StringBuilder sb = new StringBuilder();
			int off = findWord(cs, 0, sb);
			field.setName(Strings.trim(sb));
			// Set type
			if (off > 0) {
				sb = new StringBuilder();
				off = findWord(cs, off, sb);
				field.setType(Strings.trim(sb));
			}
			// Set [PK|+|!|UNIQUE] OR default value
			if (off > 0) {
				sb = new StringBuilder();
				off = findWord(cs, off, sb);
				String s = Strings.trim(sb).toUpperCase();
				// If is is default value, set off to -1 to cancle the next
				// calling findWord()
				Matcher matcher = DEFAULTVAL.matcher(Strings.trim(sb));
				if (matcher.find()) {
					field.setDefaultValue(matcher.group(2));
					off = -1;
				} else {
					matcher = DECORATORS.matcher(s);
					while (matcher.find()) {
						String ss = matcher.group();
						if ("PK".equals(ss))
							field.setPrimaryKey(true);
						else if ("UNIQUE".equals(ss))
							field.setUnique(true);
						else if ("!".equals(ss))
							field.setNotNull(true);
						else if ("~".equals(ss))
							field.setUnsign(true);
						else if ("+".equals(ss))
							field.setAutoIncreament(true);
					}
				}
			}
			// Set default value
			if (off > 0) {
				sb = new StringBuilder();
				findWord(cs, off, sb);
				Matcher matcher = DEFAULTVAL.matcher(Strings.trim(sb));
				if (matcher.find())
					field.setDefaultValue(matcher.group(2));
			}
			return field;
		}

		int findWord(char[] cs, int off, StringBuilder sb) {
			if (off >= cs.length)
				return -1;
			int i = off;
			for (; i < cs.length; i++) {
				char c = cs[i];
				if (c > 0 && c <= 0x20)
					if (sb.length() == 0)
						continue;
					else
						break;
				sb.append(c);
			}
			return ++i;
		}
	}

	static Pattern DECORATORS = Pattern.compile("PK|[+!~]|UNIQUE");
	static Pattern DEFAULTVAL = Pattern.compile("^([<])(.+)([>])$");
}
