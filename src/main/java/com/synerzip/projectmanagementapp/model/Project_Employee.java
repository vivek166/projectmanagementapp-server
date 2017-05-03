package com.synerzip.projectmanagementapp.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="project_employee")
public class Project_Employee {
	@Id
	private long projectEmpId;
	private long projectId;
	private long empId;
	
	public Project_Employee() {

	}
	public long getProjectEmpId() {
		return projectEmpId;
	}
	public void setProjectEmpId(long projectEmpId) {
		this.projectEmpId = projectEmpId;
	}
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
