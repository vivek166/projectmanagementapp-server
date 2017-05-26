package com.synerzip.projectmanagementapp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

@Entity
@Table(name = "user")
@Indexed
public class User {

	@Id
	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "user_id")
	private String userId;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "user_name")
	private String userName;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "company_name")
	private String companyName;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "user_password")
	private String userPassword;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", userName=" + userName + ", companyName=" + companyName + ", userPassword="
				+ userPassword + "]";
	}

}