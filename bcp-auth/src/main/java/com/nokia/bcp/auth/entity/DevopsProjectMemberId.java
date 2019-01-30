package com.nokia.bcp.auth.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DevOps 项目成员的主键
 * 
 * @author wangyueda
 *
 */
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DevopsProjectMemberId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "project_id")
	private Long projectId;

	@Column(name = "member_id")
	private Long memberId;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		DevopsProjectMemberId that = (DevopsProjectMemberId) o;
		return Objects.equals(projectId, that.projectId) && Objects.equals(memberId, that.memberId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(projectId, memberId);
	}

}
