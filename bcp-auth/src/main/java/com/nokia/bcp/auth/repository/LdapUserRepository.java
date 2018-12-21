package com.nokia.bcp.auth.repository;

import org.springframework.data.ldap.repository.LdapRepository;

import com.nokia.bcp.auth.entity.LdapUser;

public interface LdapUserRepository extends LdapRepository<LdapUser> {

	LdapUser findByAccount(String account);

	LdapUser findByAccountOrEmail(String account, String email);

	boolean authenticate(String account, String password);

	void changePassword(String account, String newPassword);

}
