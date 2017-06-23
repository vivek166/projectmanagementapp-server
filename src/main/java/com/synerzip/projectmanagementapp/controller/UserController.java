package com.synerzip.projectmanagementapp.controller;

import java.io.IOException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import com.synerzip.projectmanagementapp.authentication.Secure;
import com.synerzip.projectmanagementapp.httpmethods.Patch.PATCH;
import com.synerzip.projectmanagementapp.model.ChangePassword;
import com.synerzip.projectmanagementapp.model.Company;
import com.synerzip.projectmanagementapp.model.User;
import com.synerzip.projectmanagementapp.model.UserCredentials;
import com.synerzip.projectmanagementapp.serviceimplementation.UserServicesImplementation;

@Path("/user")
public class UserController {

	UserServicesImplementation service = new UserServicesImplementation();

	@GET
	@Secure
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	public String get(@PathParam("id") long id, @Context SecurityContext securityContext)
			throws JsonGenerationException, JsonMappingException, IOException {
		User user = (User) securityContext.getUserPrincipal();
		long companyId = user.getCompany().getCompanyId();
		return new ObjectMapper().writeValueAsString(service.get(id, companyId));
	}

	@GET
	@Secure
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}/assignedproject")
	public String assigned(@PathParam("id") long id) throws JsonGenerationException, JsonMappingException, IOException {
		return new ObjectMapper().writeValueAsString(service.assigned(id));
	}

	@GET
	@Secure
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/login/profile")
	public String profile(@QueryParam("userId") long userId)
			throws JsonGenerationException, JsonMappingException, IOException {
		return new ObjectMapper().writeValueAsString(service.profile(userId));
	}

	@GET
	@Secure
	@Produces(MediaType.APPLICATION_JSON)
	public String gets(@DefaultValue("0") @QueryParam("start") int start,
			@DefaultValue("5") @QueryParam("size") int size, @DefaultValue("") @QueryParam("query") String query,
			@Context SecurityContext securityContext)
			throws JsonGenerationException, JsonMappingException, IOException {
		User user = (User) securityContext.getUserPrincipal();
		long companyId = user.getCompany().getCompanyId();
		return new ObjectMapper().writeValueAsString(service.gets(start, size, query, companyId));
	}

	@GET
	@Secure
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public String search(@DefaultValue("0") @QueryParam("start") int start,
			@DefaultValue("5") @QueryParam("size") int size, @DefaultValue("") @QueryParam("query") String query,
			@Context SecurityContext securityContext)
			throws JsonGenerationException, JsonMappingException, IOException {
		User user = (User) securityContext.getUserPrincipal();
		long companyId = user.getCompany().getCompanyId();
		return new ObjectMapper().writeValueAsString(service.search(start, size, query, companyId));
	}

	@GET
	@Secure
	@Path("/filter")
	@Produces(MediaType.APPLICATION_JSON)
	public String getEmployees(@DefaultValue("0") @QueryParam("start") int start,
			@DefaultValue("5") @QueryParam("size") int size, @QueryParam("query") String query,
			@Context SecurityContext securityContext)
			throws JsonGenerationException, JsonMappingException, IOException {
		User user = (User) securityContext.getUserPrincipal();
		long companyId = user.getCompany().getCompanyId();
		return new ObjectMapper().writeValueAsString(service.getEmployees(start, size, query, companyId));
	}

	@POST
	@Secure
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(User user, @Context SecurityContext securityContext) {
		User sessionUser = (User) securityContext.getUserPrincipal();
		long companyId = sessionUser.getCompany().getCompanyId();
		return Response.ok().entity(service.add(user, companyId)).build();
	}

	@POST
	@Secure
	@Path("/changepassword")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response changePassword(ChangePassword data, @Context SecurityContext securityContext) {
		User user = (User) securityContext.getUserPrincipal();
		String userName = user.getEmail();
		return Response.ok().entity(service.changePassword(userName, data)).build();
	}

	@POST
	@Secure
	@Path("/assignproject")
	@Produces(MediaType.TEXT_PLAIN)
	public Response assignProject(@QueryParam("userId") long userId, @QueryParam("projectId") long projectId) {
		return Response.ok().entity(service.assignProject(userId, projectId)).build();
	}

	@DELETE
	@Secure
	@Produces(MediaType.TEXT_PLAIN)
	@Path("{id}")
	public Response delete(@PathParam("id") long id, @Context SecurityContext securityContext) {
		User user = (User) securityContext.getUserPrincipal();
		long companyId = user.getCompany().getCompanyId();
		return Response.ok().entity(service.delete(id, companyId)).build();
	}

	@DELETE
	@Secure
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}/deletetoken")
	public Response token(@PathParam("id") long id) {
		return Response.ok().entity(service.token(id)).build();
	}

	@PUT
	@Secure
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response update(User user, @PathParam("id") long id) {
		return Response.ok().entity(service.update(user, id)).build();
	}

	@PATCH
	@Secure
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response patch(User user, @PathParam("id") long id) {
		return Response.ok().entity(service.patch(user, id)).build();
	}

	@POST
	@Path("/authentication")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response userAuthentication(UserCredentials userCredentials) {
		return Response.ok().entity(service.userAuthentication(userCredentials)).build();
	}

}