package com.synerzip.projectmanagementapp.authentication;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
import com.synerzip.projectmanagementapp.model.Token;

@Provider
@SECURED
public class UserAuthenticationFilter implements ContainerRequestFilter {

<<<<<<< HEAD
	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {

		String authorizationHeader = requestContext
				.getHeaderString(HttpHeaders.AUTHORIZATION);

		if (StringUtils.isEmpty(authorizationHeader)
				|| !authorizationHeader.startsWith("Bearer ")) {
			throw new NotAuthorizedException(
					"Authorization header must be provided");
=======
	static final Logger logger = Logger.getLogger(UserAuthenticationFilter.class);

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		logger.info("starting authentication");

		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		if (StringUtils.isEmpty(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
			logger.error("Authorization header must be provided ");
			throw new NotAuthorizedException("Authorization header must be provided");
>>>>>>> 6ca684102c93a879cbab27e7bba963124af17a3d
		}

		String token = authorizationHeader.substring("Bearer".length()).trim();

		try {

			validateToken(token);
<<<<<<< HEAD

		} catch (Exception e) {
			requestContext.abortWith(Response.status(
					Response.Status.UNAUTHORIZED).build());
=======
			logger.info("authentication successfully done");

		} catch (Exception e) {
			logger.error("UnAuthorization Access");
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
>>>>>>> 6ca684102c93a879cbab27e7bba963124af17a3d
		}
	}

	private void validateToken(String token) throws Exception {
<<<<<<< HEAD
		if (!StringUtils.isEmpty(token) && !StringUtils.isWhitespace(token)) {
			Session session = HibernateUtils.getSession();
			Query query = session
					.createQuery("select token from Token where token =:token");
			query.setParameter("token", token);
			String result = (String) query.uniqueResult();
			if (token.equals(result)) {
				String filename = "token.txt";
				Token serializedObject = new Token();
				FileOutputStream fileOutputStream = null;
				ObjectOutputStream objectOutputStream = null;
				try {
					fileOutputStream = new FileOutputStream(filename);
					objectOutputStream = new ObjectOutputStream(
							fileOutputStream);
					objectOutputStream.writeObject(serializedObject);
					objectOutputStream.flush();
					objectOutputStream.close();
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
				return;
			} else {
				throw new NotAuthorizedException(
						"validation failed : invalid token");
			}
		} else {
			throw new NotAuthorizedException(
					"validation failed : please provide a valid token ");
=======
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
>>>>>>> 6ca684102c93a879cbab27e7bba963124af17a3d
		}
	}
}