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
@Table(name = "employee")
public class Employee {

	@Id
	@GeneratedValue
	@Column(name = "emp_id")
	private long empId;
	@Column(name = "emp_name")
	private String empName;
	@Column(name = "emp_department")
	private String empDepartment;
	@Column(name = "emp_subject")
	private String empSubjects;
	
	@Transient
	private List<Integer> projectIds;
	
	
	@OneToMany(mappedBy="employee")
	private List<ProjectEmployee> projectEmployees;

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

	public List<Integer> getProjectIds() {
		return projectIds;
	}

	public void setProjectIds(List<Integer> projectIds) {
		this.projectIds = projectIds;
	}

	public List<ProjectEmployee> getProjectEmployees() {
		return projectEmployees;
	}

	public void setProjectEmployees(List<ProjectEmployee> projectEmployees) {
		this.projectEmployees = projectEmployees;
	}
}
