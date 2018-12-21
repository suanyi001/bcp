package com.nokia.bcp.auth.repository;

import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.codec.digest.Sha2Crypt;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;

import com.nokia.bcp.auth.entity.LdapUser;

public class LdapUserRepositoryImpl {

	@Autowired
	private LdapTemplate ldapTemplate;

	@Autowired
	private LdapUserRepository userRepository;

	private Random random = new Random(System.currentTimeMillis());

	public boolean authenticate(String account, String password) {
		LdapUser user = userRepository.findByAccountOrEmail(account, account);
		if (null != user) {
			boolean authenticate = ldapTemplate.authenticate(user.getDn(), "(objectclass=person)", password);
			return authenticate;
		}
		return false;
	}

	public void changePassword(String account, String newPassword) {
		System.out.println(">>>>> changePassword: " + account);
		LdapUser user = userRepository.findByAccountOrEmail(account, account);
		if (null != user) {
			int randomCode = random.nextInt(5);
			System.out.println(">>>>>>>> randomCode: " + randomCode);
			String password = null;
			switch (randomCode) {
			case 0:
				password = "{MD5}" + Base64.encodeBase64String(DigestUtils.md5(newPassword));
				break;
			case 1:
				password = "{SHA}" + Base64.encodeBase64String(DigestUtils.sha1(newPassword));
				break;
			case 2:
				password = "{CRYPT}" + Md5Crypt.md5Crypt(newPassword.getBytes());
				break;
			case 3:
				password = "{CRYPT}" + Sha2Crypt.sha256Crypt(newPassword.getBytes());
				break;
			case 4:
				password = "{CRYPT}" + Sha2Crypt.sha512Crypt(newPassword.getBytes());
				break;
			}
			user.setPassword(password);
			ldapTemplate.update(user);
		}
	}

}
