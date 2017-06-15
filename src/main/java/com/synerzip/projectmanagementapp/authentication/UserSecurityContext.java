package com.synerzip.projectmanagementapp.authentication;

import javax.ws.rs.core.SecurityContext;
import com.synerzip.projectmanagementapp.model.User;
import java.security.Principal;

public class UserSecurityContext implements SecurityContext {
	private User user;
	private String scheme;

	public UserSecurityContext(User user, String scheme) {
        this.user = user;
        this.scheme = scheme;
    }

	@Override
	public Principal getUserPrincipal() {
		return this.user;
	}

	@Override
	public boolean isUserInRole(String s) {
		if (user.getType() != null) {
			return user.getType().contains(s);
		}
		return false;
	}

	@Override
	public boolean isSecure() {
		return "https".equals(this.scheme);
	}

	@Override
	public String getAuthenticationScheme() {
		return SecurityContext.BASIC_AUTH;
	}
}