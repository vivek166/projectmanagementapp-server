package com.synerzip.projectmanagementapp.authentication;

import java.io.IOException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import com.synerzip.projectmanagementapp.authentication.Secured.SECURED;
import com.synerzip.projectmanagementapp.dbconnection.HibernateUtils;

@Provider
@SECURED
public class UserAuthenticationFilter implements ContainerRequestFilter {

	static final Logger logger = Logger.getLogger(UserAuthenticationFilter.class);

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		logger.info("starting authentication");

		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		if (StringUtils.isEmpty(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
			logger.error("Authorization header must be provided ");
			throw new NotAuthorizedException("Authorization header must be provided");
		}

		String token = authorizationHeader.substring("Bearer".length()).trim();

		try {

			validateToken(token);
			logger.info("authentication successfully done");

		} catch (Exception e) {
			logger.error("UnAuthorization Access");
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
	}

	private void validateToken(String token) throws Exception {
		Session session = HibernateUtils.getSession();
		logger.info("starting token  validation");
		try {
			Query query = session.createQuery("select token from Token where token =:token");
			query.setParameter("token", token);
			String result = (String) query.uniqueResult();
			if (token.equals(result)) {
				logger.info("this is valid token");
				return;
			}
			logger.info("this is not a valid token");
			throw new NotAuthorizedException("validation failed : invalid token");
		} catch (HibernateException exception) {
			exception.printStackTrace();
		} finally {
			session.close();
		}
	}
}