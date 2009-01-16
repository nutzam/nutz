package com.zzh.algo.find;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Finder {
	/**
	 * Find a object in sublist (begin->end) refer one given object. The list be
	 * a sorted list (ASC) by a <b>Comparator</b> (<i>or each item in the list
	 * is <b>Comparable</b></i>). <br>
	 * <br>
	 * It will return the first item in list, which less than or equal the refer
	 * and most close to the refer. <br>
	 * <br>
	 * <b>For example</b> <br>
	 * If a list like that (<b>List list;</b>): <ol start="0"> <li>abc <li>123ww
	 * <li>kkqqcml <li>axywqerw </ol> Obviously, the list order by each item
	 * (String) length, from the shortest to longest. So a Comparator will be
	 * provided(<b>Comparator comp;</b>), it will compare to String's length. <br>
	 * If -> <i>findIndex(list,comp,"xyzxyz",0,3);</i> -> return 1. <br>
	 * <br>
	 * 
	 * @param list
	 *            : List object
	 * @param refer
	 *            : Refer object
	 * @param comp
	 *            : Comparator
	 * @param begin
	 *            : begin index of the list, if it less than 0, take it as 0.
	 * @param end
	 *            : end index of the list, if it >= list.size(), take it as
	 *            size()-1.
	 * 
	 * @return The index of object in list, which less than or equal the refer
	 *         and most close to the refer. If end less than begin, return -1.
	 *         If can not find the index return -1 also.
	 */
	public static int findIndexInASCList(List<?> list, Object refer,
			Comparator<Object> comp, int begin, int end) {
		if (null == refer)
			return -1;
		if (null == list)
			return -1;
		if (list.size() == 0)
			return -1;
		if (begin < 0)
			begin = 0;
		if (end >= list.size())
			end = list.size() - 1;
		if (end < begin)
			return -1;
		return findIndexRegularly(list, refer, comp, begin, end);
	}

	public static int findIndex(List<?> list, Object obj) {
		if (null == list)
			return -1;
		if (list.size() == 0)
			return -1;
		Iterator<?> it = list.iterator();
		int i = 0;
		while (it.hasNext()) {
			Object o = it.next();
			if (o == obj)
				return i;
			else if (null == o)
				continue;
			else if (o.equals(obj))
				return i;
			i++;
		}
		return -1;
	}

	private static int findIndexRegularly(List<?> list, Object refer,
			Comparator<Object> comp, int begin, int end) {
		if (end == begin) {
			if (comp.compare(refer, list.get(end)) > 0) {
				return end;
			} else
				return -1;
		} else if (end == begin + 1) {
			if (comp.compare(refer, list.get(end)) > 0)
				return end;
			if (comp.compare(refer, list.get(begin)) > 0)
				return begin;
		}
		int middle = (begin + end) / 2;
		int r = comp.compare(refer, list.get(middle));
		if (r == 0) {
			return middle;
		} else if (r > 0) {
			return findIndexInASCList(list, refer, comp, middle, end);
		}
		return findIndexInASCList(list, refer, comp, begin, middle);
	}

	public static boolean equals(List<?> l1, List<?> l2) {
		if (l1 == l2)
			return true;
		if (null == l1 || null == l2)
			return false;
		if (l1.size() != l2.size())
			return false;
		Iterator<?> it1 = l1.iterator();
		Iterator<?> it2 = l2.iterator();
		while (it1.hasNext()) {
			Object v1 = it1.next();
			Object v2 = it2.next();
			if (!v1.equals(v2))
				return false;
		}
		return true;
	}

	public static <T> String toString(List<T> list, String delimiter) {
		if (null == list)
			return null;
		if (list.size() == 0)
			return "";
		StringBuffer sb = new StringBuffer();
		Iterator<T> it = list.iterator();
		sb.append(it.next().toString());
		while (it.hasNext()) {
			sb.append(delimiter);
			sb.append(it.next().toString());
		}
		return sb.toString();
	}

	public static <T> String toString(List<T> list) {
		return toString(list, ",");
	}
	
}
