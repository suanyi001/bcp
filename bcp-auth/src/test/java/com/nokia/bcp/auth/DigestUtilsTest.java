package com.nokia.bcp.auth;

import org.apache.commons.codec.digest.Crypt;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.codec.digest.Sha2Crypt;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Test;

public class DigestUtilsTest {

	@Test
	public void test() {
		System.out.println("md5: " + Base64.encodeBase64String(DigestUtils.md5("123987")));
		System.out.println("sha1: " + Base64.encodeBase64String(DigestUtils.sha1("123987")));
		System.out.println("sha256: " + Base64.encodeBase64String(DigestUtils.sha256("123987")));
		System.out.println("sha512: " + Base64.encodeBase64String(DigestUtils.sha512("123987")));
		System.out.println("crypt: " + Crypt.crypt("123987"));
		System.out.println("md5Crypt: " + Md5Crypt.md5Crypt("123987".getBytes()));
		System.out.println("sha256Crypt: " + Sha2Crypt.sha256Crypt("123987".getBytes()));
		System.out.println("sha512Crypt: " + Sha2Crypt.sha512Crypt("123987".getBytes()));
	}

}
