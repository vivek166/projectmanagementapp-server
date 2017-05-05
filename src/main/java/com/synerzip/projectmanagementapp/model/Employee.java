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
	
	
	public List<Integer> getProject_id() {
		return project_id;
	}

	public void setProject_id(List<Integer> project_id) {
		this.project_id = project_id;
	}

	@Transient
	private List<Integer> project_id;

	@OneToMany(mappedBy="employee")
	private List<Project_Employee> project_employees;

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

	public List<Project_Employee> getProject_employees() {
		return project_employees;
	}

	public void setProject_employees(List<Project_Employee> project_employees) {
		this.project_employees = project_employees;
	}

	@Override
	public String toString() {
		return "Employee [empId=" + empId + ", empName=" + empName + ", empDepartment=" + empDepartment
				+ ", empSubjects=" + empSubjects + ", project_employees=" + project_employees + "]";
	}
	
	
	
	
}
