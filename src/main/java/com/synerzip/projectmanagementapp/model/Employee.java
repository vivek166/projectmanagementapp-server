package com.synerzip.projectmanagementapp.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

@Entity@Table(name = "employee")@Indexed
public class Employee {

	@Id@GeneratedValue@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)@Column(name = "emp_id")
	private long empId;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)@Column(name = "emp_name")
	private String empName;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)@Column(name = "emp_department")
	private String empDepartment;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)@Column(name = "emp_subject")
	private String empSubjects;

	@Transient
	private List < Integer > projectIds;

	/*@OneToMany(mappedBy="employee")
	private List<ProjectEmployee> projectEmployees;*/

	public Employee() {

}

	public long getEmpId() {
		return empId;
	}

	public void setEmpId(long empId) {
		this.empId = empId;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getEmpDepartment() {
		return empDepartment;
	}

	public void setEmpDepartment(String empDepartment) {
		this.empDepartment = empDepartment;
	}

	public String getEmpSubjects() {
		return empSubjects;
	}

	public void setEmpSubjects(String empSubjects) {
		this.empSubjects = empSubjects;
	}

	public List < Integer > getProjectIds() {
		return projectIds;
	}

	public void setProjectIds(List < Integer > projectIds) {
		this.projectIds = projectIds;
	}

	@Override
	public String toString() {
		return "Employee [empId=" + empId + ", empName=" + empName + ", empDepartment=" + empDepartment + ", empSubjects=" + empSubjects + ", projectIds=" + projectIds + "]";
	}

	/*public List<ProjectEmployee> getProjectEmployees() {
		return projectEmployees;
	}

	public void setProjectEmployees(List<ProjectEmployee> projectEmployees) {
		this.projectEmployees = projectEmployees;
	}*/

}