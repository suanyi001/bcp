package com.nokia.bcp.auth.utils;

import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.codec.digest.Sha2Crypt;
import org.apache.tomcat.util.codec.binary.Base64;

public class HashedPwdUtils {

	private static Random random = new Random(System.currentTimeMillis());

	public static String createLdapPwd(String plaintext) {
		String password = null;
		int randomCode = random.nextInt(5);
		switch (randomCode) {
		case 0:
			password = "{MD5}" + Base64.encodeBase64String(DigestUtils.md5(plaintext));
			break;
		case 1:
			password = "{SHA}" + Base64.encodeBase64String(DigestUtils.sha1(plaintext));
			break;
		case 2:
			password = "{CRYPT}" + Md5Crypt.md5Crypt(plaintext.getBytes());
			break;
		case 3:
			password = "{CRYPT}" + Sha2Crypt.sha256Crypt(plaintext.getBytes());
			break;
		case 4:
			password = "{CRYPT}" + Sha2Crypt.sha512Crypt(plaintext.getBytes());
			break;
		}
		return password;
	}

}
