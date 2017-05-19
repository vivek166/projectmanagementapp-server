package com.synerzip.projectmanagementapp.model;

import java.io.Serializable;
import javax.persistence.Embeddable;

@Embeddable
public class ProjectEmployeeId implements Serializable {
	private static final long serialVersionUID = 1L;

	private long projectId;
	
	private long empId;

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public long getEmpId() {
		return empId;
	}

	public void setEmpId(long empId) {
		this.empId = empId;
	}
	
}
