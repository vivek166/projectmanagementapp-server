package com.synerzip.projectmanagementapp.model;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "project")
public class Project {

	@Id
	@GeneratedValue
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
	
	@Transient
	private List<Integer> empIds;

	@OneToMany(mappedBy = "project")
	private List<ProjectEmployee> projectEmployees;

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

	public List<Integer> getEmpIds() {
		return empIds;
	}

	public void setEmpIds(List<Integer> empIds) {
		this.empIds = empIds;
	}

	public List<ProjectEmployee> getProjectEmployees() {
		return projectEmployees;
	}

	public void setProjectEmployees(List<ProjectEmployee> projectEmployees) {
		this.projectEmployees = projectEmployees;
	}
}