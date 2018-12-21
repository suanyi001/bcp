package com.nokia.bcp.auth.entity;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import lombok.Data;

// @Entry(base = "ou=people,dc=nokia,dc=com", objectClasses = "inetOrgPerson")
//@Entry(objectClasses = { "inetOrgPerson", "organizationalPerson", "person", "top" })
//@Entry(objectClasses = { "inetOrgPerson" })
@Entry(objectClasses = { "inetOrgPerson", "posixAccount", "top" }, base = "ou=people")
@Data
public class LdapUser {

	@Id
	private Name dn;

	@DnAttribute(value = "uid", index = 1)
	private String uid;

	@Attribute(name = "cn")
	private String account;

	@Attribute(name = "sn")
	private String username;

	@Attribute(name = "mail")
	private String email;

	@Attribute(name = "userPassword")
	private String password;

	@Attribute(name = "gidNumber")
	private String gidNumber = "0";

	@Attribute(name = "uidNumber")
	private String uidNumber = "1";

	@Attribute(name = "homeDirectory")
	private String homeDirectory = "";

}
