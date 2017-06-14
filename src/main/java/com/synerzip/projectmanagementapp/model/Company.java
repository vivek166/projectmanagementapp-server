package com.synerzip.projectmanagementapp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

@Entity
@Indexed
@Table(name = "company")
public class Company {

	@Id
	@GeneratedValue
	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "company_id")
	private long companyId;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "company_name", unique = true)
	private String companyName;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "comapny_address")
	private String companyAddress;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "company_cont_num")
	private String companyContactNumber;

	public Company() {
	}

	
	public Company(long companyId, String companyName, String companyAddress, String companyContactNumber) {
		super();
		this.companyId = companyId;
		this.companyName = companyName;
		this.companyAddress = companyAddress;
		this.companyContactNumber = companyContactNumber;
	}


	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public String getCompanyContactNumber() {
		return companyContactNumber;
	}

	public void setCompanyContactNumber(String companyContactNumber) {
		this.companyContactNumber = companyContactNumber;
	}

	@Override
	public String toString() {
		return "Company [companyId=" + companyId + ", companyName="
				+ companyName + ", companyAddress=" + companyAddress
				+ ", companyContactNumber=" + companyContactNumber + "]";
	}

}
