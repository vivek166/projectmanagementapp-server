package com.synerzip.projectmanagementapp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "token")
public class Token {

	@Id
	@GeneratedValue
	@Column(name = "token_id")
	private long tokenId;

	@Column(name = "token")
	private String token;

	public long getTokenId() {
		return tokenId;
	}

	public void setTokenId(long tokenId) {
		this.tokenId = tokenId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "Token [tokenId=" + tokenId + ", token=" + token + "]";
	}
}
