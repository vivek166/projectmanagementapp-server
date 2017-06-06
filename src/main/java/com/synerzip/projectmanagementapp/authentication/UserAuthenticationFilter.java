package com.synerzip.projectmanagementapp.authentication;

import java.io.IOException;
import java.security.Principal;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import com.synerzip.projectmanagementapp.authentication.Secured.SECURED;
import com.synerzip.projectmanagementapp.dbconnection.HibernateUtils;
import com.synerzip.projectmanagementapp.model.Token;

@Provider
@SECURED
public class UserAuthenticationFilter implements ContainerRequestFilter {

	@Context
	SecurityContext securityContext;
	static final Logger logger = Logger.getLogger(UserAuthenticationFilter.class);

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {

		System.out.println("got request -> " + requestContext.getHeaderString(HttpHeaders.AUTHORIZATION));

		logger.info("starting authentication");

		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		if (StringUtils.isEmpty(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
			logger.error("Authorization header must be provided ");
			throw new NotAuthorizedException("Authorization header must be provided");
		}

		String token = authorizationHeader.substring("Bearer".length()).trim();

		try {

			final Token tokenObj = validateToken(token);
			final SecurityContext currentSecurityContext = requestContext.getSecurityContext();
			requestContext.setSecurityContext(new SecurityContext() {

				@Override
				public Principal getUserPrincipal() {

					return new Principal() {

						@Override
						public String getName() {
							return tokenObj.getUserName();
						}
					};
				}

				@Override
				public boolean isUserInRole(String role) {
					return true;
				}

				@Override
				public boolean isSecure() {
					return currentSecurityContext.isSecure();
				}

				@Override
				public String getAuthenticationScheme() {
					return "" + tokenObj.getToken();
				}
			});

		} catch (Exception e) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			logger.info("authentication successfully done");

		}
	}

	private Token validateToken(String token){
		Session session = HibernateUtils.getSession();
		try{
				Query query = session.createQuery("from Token where token =:token");
				query.setParameter("token", token);
				Token result = (Token) query.uniqueResult();
				if (token.equals(result.getToken())) {
					return result;
					}
		}catch(Exception exception){
			throw new NotAuthorizedException("authentication failed : invalid token");
		}finally{
			session.close();
		}
		return null;
	}
}