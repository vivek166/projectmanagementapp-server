package com.synerzip.projectmanagementapp.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

@Entity
@Table(name = "employee")
@Indexed
public class Employee {

	@Id
	@GeneratedValue
	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "emp_id")
	private long empId;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "emp_name", unique = true, nullable = false)
	private String empName;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "emp_department", nullable = false)
	private String empDepartment;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "emp_subjects", nullable = false)
	private String empSubjects;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "emp_type", nullable = false)
	private String employeeType;

	@Transient
	@Column(name = "project_ids")
	private List<Integer> projectIds;

	@Transient
	@Column(name = "company_id")
	private long companyId;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "company_id", nullable = false)
	private Company company;

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

	public String getEmployeeType() {
		return employeeType;
	}

	public void setEmployeeType(String employeeType) {
		this.employeeType = employeeType;
	}

	public List<Integer> getProjectIds() {
		return projectIds;
	}

	public void setProjectIds(List<Integer> projectIds) {
		this.projectIds = projectIds;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public String toString() {
		return "Employee [empId=" + empId + ", empName=" + empName
				+ ", empDepartment=" + empDepartment + ", empSubjects="
				+ empSubjects + ", employeeType=" + employeeType
				+ ", projectIds=" + projectIds + ", companyId=" + companyId
				+ ", company=" + company + "]";
	}

}