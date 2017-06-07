package com.synerzip.projectmanagementapp.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "token")
public class Token implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "token_id")
	private long tokenId;

	@Column(name = "token", unique=true)
	private String token;

	@Column(name = "expiry_time")
	private Date expiryTime;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	public Token() {
	}

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

	public Date getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(Date expiryTime) {
		this.expiryTime = expiryTime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Token [tokenId=" + tokenId + ", token=" + token + ", expiryTime=" + expiryTime + ", user=" + user + "]";
	}
	
}
	