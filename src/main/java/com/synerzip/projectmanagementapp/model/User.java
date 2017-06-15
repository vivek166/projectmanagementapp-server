package com.synerzip.projectmanagementapp.model;

import java.security.Principal;
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
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

@Entity
@Table(name = "user")
@Indexed
@NamedQueries({ @NamedQuery(name = "getById", query = "from User where id = :id") })
public class User implements Principal {

	@Id
	@GeneratedValue
	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "id")
	private long id;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "first_name")
	private String firstName;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "last_name")
	private String lastName;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "mobile")
	private String mobile;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "skills")
	private String skills;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "type")
	private String type;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "email", unique = true)
	private String email;

	@Column(name = "password")
	private String password;

	@Transient
	@Column(name = "project_ids")
	private List<Integer> projectIds;

	@Transient
	@Column(name = "company_name")
	private String companyName;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "company_id")
	private Company company;

	public User() {

	}

	public User(String firstName, String lastName, String type, String email, String mobile,
			String skills, Company company) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.mobile = mobile;
		this.skills = skills;
		this.type = type;
		this.email = email;
		this.company = company;
	}

	
	public User(long id, String firstName, String lastName, String mobile, String skills, String type, String email) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mobile = mobile;
		this.skills = skills;
		this.type = type;
		this.email = email;
	}

	public User(long id, String firstName, String lastName, String email) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}
	
	public User(long id, String firstName, String lastName, String mobile, String skills, String type, String email,
			Company company) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mobile = mobile;
		this.skills = skills;
		this.type = type;
		this.email = email;
		this.company = company;
	}
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Integer> getProjectIds() {
		return projectIds;
	}

	public void setProjectIds(List<Integer> projectIds) {
		this.projectIds = projectIds;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", mobile=" + mobile
				+ ", skills=" + skills + ", type=" + type + ", email=" + email + ", password=" + password
				+ ", projectIds=" + projectIds + ", companyName=" + companyName + ", company=" + company + "]";
	}

	@Override
	public String getName() {
		return this.firstName + " " + this.lastName;
	}

}