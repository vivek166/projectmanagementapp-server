package com.synerzip.projectmanagementapp.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
@Entity
@Table(
		   name = "project_employee", 
		   uniqueConstraints=
			        @UniqueConstraint(columnNames={"project_id", "emp_id"})
		)
public class ProjectEmployee {
	@Id
	@GeneratedValue
	private long projectEmpId;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "project_id")
	private Project project;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "emp_id")
	private User user;

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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "ProjectEmployee [projectEmpId=" + projectEmpId + ", project=" + project + ", user=" + user + "]";
	}

}