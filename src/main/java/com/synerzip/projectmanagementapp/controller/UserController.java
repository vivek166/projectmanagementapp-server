package com.synerzip.projectmanagementapp.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import java.io.IOException;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import com.synerzip.projectmanagementapp.authentication.Secured.SECURED;
import com.synerzip.projectmanagementapp.httpmethods.Patch.PATCH;
import com.synerzip.projectmanagementapp.model.User;
import com.synerzip.projectmanagementapp.model.UserCredentials;
import com.synerzip.projectmanagementapp.serviceimplementation.UserServiceImplementation;

@Path("/user")
public class UserController {

	UserServiceImplementation service = new UserServiceImplementation();

	@GET
	@Path("/{userName}")
	@SECURED
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@PathParam("userName") String userName)
			throws JsonGenerationException, JsonMappingException, IOException {
		return new ObjectMapper().writeValueAsString(service.get(userName));
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String gets(@DefaultValue("0") @QueryParam("start") int start,
			@DefaultValue("5") @QueryParam("size") int size, @DefaultValue("") @QueryParam("query") String query)
			throws JsonGenerationException, JsonMappingException, IOException, EntityNotFoundException {
		return new ObjectMapper().writeValueAsString(service.gets(start, size, query));
	}

	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public String search(@DefaultValue("0") @QueryParam("start") int start,
			@DefaultValue("5") @QueryParam("size") int size, @QueryParam("query") String query)
			throws JsonGenerationException, JsonMappingException, IOException {
		return new ObjectMapper().writeValueAsString(service.search(start, size, query));
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(User user) {
		return Response.ok().entity(service.add(user)).build();
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{userName}")
	public Response delete(@PathParam("userName") String userName) {
		return Response.ok().entity(service.delete(userName)).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{userName}")
	public Response update(User user, @PathParam("userName") String userName) {
		return Response.ok().entity(service.update(user, userName)).build();
	}

	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{userName}")
	public Response patch(User user, @PathParam("userName") String userName) {
		return Response.ok().entity(service.patch(user, userName)).build();
	}
	
	
	@POST
	@Path("/userAuth")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response userAuthentication(UserCredentials userCredentials){
		return Response.ok().entity(service.userAuthentication(userCredentials)).build();
	}
	
	/*Basic authentication method
    @POST
	@Path("/userAuth")
	@Produces(MediaType.TEXT_PLAIN)
	public String  userAuthentication(){
		return "This is secured methods";
	}*/
}