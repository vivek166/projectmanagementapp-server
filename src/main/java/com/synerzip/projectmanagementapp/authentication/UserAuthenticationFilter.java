/*package com.synerzip.projectmanagementapp.authentication;

import java.io.IOException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import com.synerzip.projectmanagementapp.authentication.Secured.SECURED;
import com.synerzip.projectmanagementapp.dbconnection.HibernateUtils;

@Provider
@SECURED
public class UserAuthenticationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String authorizationHeader = 
            requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        if (StringUtils.isEmpty(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }
        
        String token = authorizationHeader.substring("Bearer".length()).trim();
        
        try {
        	
            validateToken(token);

        } catch (Exception e) {
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    private void validateToken(String token) throws Exception {
    	Session session = HibernateUtils.getSession();
    	Query query=session.createQuery("select token from Token where token =:token");
    	query.setParameter("token", token);
    	String result=(String) query.uniqueResult();
    	if(token.equals(result)){
    		return;
    	}
    	 throw new NotAuthorizedException("validation failed : invalid token");
    }
}

















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


*/