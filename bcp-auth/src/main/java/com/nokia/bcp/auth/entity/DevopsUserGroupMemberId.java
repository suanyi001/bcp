package com.nokia.bcp.auth.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DevOps 用户组成员的主键
 * 
 * @author wangyueda
 *
 */
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DevopsUserGroupMemberId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "user_group_id")
	private Long userGroupId;

	@Column(name = "user_id")
	private Long userId;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		DevopsUserGroupMemberId that = (DevopsUserGroupMemberId) o;
		return Objects.equals(userGroupId, that.userGroupId) && Objects.equals(userId, that.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userGroupId, userId);
	}

}
