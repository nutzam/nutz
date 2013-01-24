package org.nutz.lang.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.nutz.lang.Lang;

public class Encrypts {

	public static MessageDigest sha1() {
		try {
			return MessageDigest.getInstance("sha1");
		}
		catch (NoSuchAlgorithmException e) {
			throw Lang.noImplement(); // 不可能
		}
	}
	
	public static MessageDigest md5() {
		try {
			return MessageDigest.getInstance("md5");
		}
		catch (NoSuchAlgorithmException e) {
			throw Lang.noImplement(); // 不可能
		}
	}
}
