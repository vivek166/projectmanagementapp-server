package com.synerzip.projectmanagementapp.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FullTextFilterDef;
import org.hibernate.search.annotations.FullTextFilterDefs;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

@Entity
@Table(name = "project")
@Indexed
@FullTextFilterDefs( {
    @FullTextFilterDef(name = "companyIdFilterInProject", impl = Project.class) 
})
@NamedQueries({
		@NamedQuery(name = "getProjectById", query = "from Project where project_id = :projectid and company_id = :companyid") })
public class Project {

	@Id
	@GeneratedValue
	@Column(name = "project_id")
	private long projectId;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "project_title", unique = true)
	private String projectTitle;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "technology_used")
	private String technologyUsed;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "project_description")
	private String projectDescription;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "project_feature")
	private String projectFeature;

	@Transient
	@Column(name = "emp_ids")
	private List<Long> empIds;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "company_id")
	private Company company;

	public Project() {

	}

	public Project(long projectId, String projectTitle) {
		super();
		this.projectId = projectId;
		this.projectTitle = projectTitle;
	}

	public Project(long projectId, String projectTitle, String technologyUsed, String projectDescription,
			String projectFeature) {
		super();
		this.projectId = projectId;
		this.projectTitle = projectTitle;
		this.technologyUsed = technologyUsed;
		this.projectDescription = projectDescription;
		this.projectFeature = projectFeature;
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

	public List<Long> getEmpIds() {
		return empIds;
	}

	public void setEmpIds(List<Long> empIds) {
		this.empIds = empIds;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public String toString() {
		return "Project [projectId=" + projectId + ", projectTitle=" + projectTitle + ", technologyUsed="
				+ technologyUsed + ", projectDescription=" + projectDescription + ", projectFeature=" + projectFeature
				+ ", empIds=" + empIds + ", company=" + company + "]";
	}

}