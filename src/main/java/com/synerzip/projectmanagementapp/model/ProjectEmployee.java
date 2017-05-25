package com.synerzip.projectmanagementapp.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "project_employee")
public class ProjectEmployee {
	@Id
	@GeneratedValue
	private long projectEmpId;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "emp_id", nullable = false)
	private Employee employee;

	public ProjectEmployee() {
	}

	public long getProjectEmpId() {
		return projectEmpId;
	}

	public void setProjectEmpId(long projectEmpId) {
		this.projectEmpId = projectEmpId;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@Override
	public String toString() {
		return "ProjectEmployee [projectEmpId=" + projectEmpId + ", project=" + project + ", employee=" + employee
				+ "]";
	}

}