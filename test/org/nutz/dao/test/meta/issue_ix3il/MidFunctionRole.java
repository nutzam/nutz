package org.nutz.dao.test.meta.issue_ix3il;

import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Table;

@Table("mid_function_role")
@PK({ "modelId", "roleId" })
public class MidFunctionRole {
	/**
	 * 模型ID
	 */
	private Long modelId;
	/**
	 * 角色ID
	 */
	private Long roleId;

	public MidFunctionRole() {
		super();
	}

	public MidFunctionRole(Long modelId, Long roleId) {
		super();
		this.modelId = modelId;
		this.roleId = roleId;
	}

	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

}
