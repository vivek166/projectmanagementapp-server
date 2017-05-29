package com.synerzip.projectmanagementapp.authentication;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.internal.util.Base64;

@Provider
public class UserAuthentication implements ContainerRequestFilter {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String AUTHORIZATION_HEADER_PREFIX = "Basic ";
	private static final String SECURED_URL_PREFIX = "user/userAuth";

	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		if (requestContext.getUriInfo().getPath().contains(SECURED_URL_PREFIX)) {
			List<String> authHeader = requestContext.getHeaders().get(
					AUTHORIZATION_HEADER);
			if (authHeader.size() > 0) {
				String authToken = authHeader.get(0);
				authToken = authToken.replaceFirst(AUTHORIZATION_HEADER_PREFIX,
						"");
				String decodeString = Base64.decodeAsString(authToken);
				StringTokenizer tokenizer = new StringTokenizer(decodeString,
						":");
				String userName = tokenizer.nextToken();
				String userPassword = tokenizer.nextToken();
				if ("user".equals(userName) && "password".equals(userPassword)) {
					return;
				}
			}
			Response unauthorizationstatus = Response
					.status(Response.Status.UNAUTHORIZED)
					.entity("user can't access this resource").build();
			requestContext.abortWith(unauthorizationstatus);
		}
	}
}