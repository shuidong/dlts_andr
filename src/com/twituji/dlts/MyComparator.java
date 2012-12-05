package com.twituji.dlts;

import java.io.UnsupportedEncodingException;
import java.util.Comparator;

public class MyComparator implements Comparator {

	final static String CHAR_SET = "gb2312";
	final static int BY_TITLE = 0;
	final static int BY_DES = 1;

	int sortFlg = BY_TITLE;

	public MyComparator(int sortField) {
		super();
		sortFlg = sortField;
	}

	@Override
	public int compare(Object object1, Object object2) {
		String c1, c2;
		if (BY_DES == sortFlg) {
			c2 = (String) ((Item) object1).getDescription();
			c1 = (String) ((Item) object2).getDescription();
		} else {
			c1 = (String) ((Item) object1).getTitle();
			c2 = (String) ((Item) object2).getTitle();
		}
		
		/*
		 * Comparator cmp = Collator.getInstance(java.util.Locale.CHINA); return
		 * cmp.compare(c1, c2);
		 */
		return getHexString(c1.substring(0, 1), CHAR_SET).compareTo(
				getHexString(c2.substring(0, 1), CHAR_SET));

	}

	/**
	 * convert to string to the hex-value
	 * @param s
	 * @param charset
	 * @return
	 */
	public static String getHexString(String s, String charset) {
		byte[] b = null;
		StringBuffer sb = new StringBuffer();
		try {
			b = s.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < b.length; i++) {
			sb.append(Integer.toHexString(b[i] & 0xFF));
		}
		return sb.toString();
	}

}
