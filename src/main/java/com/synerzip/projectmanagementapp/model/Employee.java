package com.synerzip.projectmanagementapp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "employee")
public class Employee {

	@Id
	@Column(name = "emp_id")
	private long empId;
	@Column(name = "emp_name")
	private String empName;
	@Column(name = "emp_department")
	private String empDepartment;
	@Column(name = "emp_subject")
	private String empSubjects;

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

	
}
