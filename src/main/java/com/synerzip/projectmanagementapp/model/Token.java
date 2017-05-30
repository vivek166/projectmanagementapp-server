package com.synerzip.projectmanagementapp.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "token")
public class Token {

	@Id
	@Column(name = "token_id")
	private String tokenId;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "token")
	private String token;

	@Column(name = "expiry_time")
	private Date expiryTime;

	public Token() {
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(Date expiryTime) {
		this.expiryTime = expiryTime;
	}

	@Override
	public String toString() {
		return "Token [tokenId=" + tokenId + ", userName=" + userName + ", token=" + token + ", expiryTime="
				+ expiryTime + "]";
	}

}
