package com.synerzip.projectmanagementapp.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "project")
public class Project {

	@Id
	@Column(name = "project_id")
	private long projectId;
	@Column(name = "project_title")
	private String projectTitle;
	@Column(name = "technology_used")
	private String technologyUsed;
	@Column(name = "project_description")
	private String projectDescription;
	@Column(name = "project_feature")
	private String projectFeature;

	@ManyToMany(targetEntity = Employee.class)
	@JoinTable(name = "project_employee")
	private Set<Employee> employee;

	public Project() {

	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public String getProjectTitle() {
		return projectTitle;
	}

	public void setProjectTitle(String projectTitle) {
		this.projectTitle = projectTitle;
	}

	public String getTechnologyUsed() {
		return technologyUsed;
	}

	public void setTechnologyUsed(String technologyUsed) {
		this.technologyUsed = technologyUsed;
	}

	public String getProjectDescription() {
		return projectDescription;
	}

	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}

	public String getProjectFeature() {
		return projectFeature;
	}

	public void setProjectFeature(String projectFeature) {
		this.projectFeature = projectFeature;
	}

	public Set<Employee> getEmployee() {
		return employee;
	}

	public void setEmployee(Set<Employee> employee) {
		this.employee = employee;
	}
}