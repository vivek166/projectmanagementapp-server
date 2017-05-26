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
import com.synerzip.projectmanagementapp.httpmethods.Patch.PATCH;
import com.synerzip.projectmanagementapp.model.Project;
import com.synerzip.projectmanagementapp.serviceimplementation.ProjectServiceImplementation;

@Path("/project")
public class ProjectController {

	ProjectServiceImplementation service = new ProjectServiceImplementation();

	@GET
	@Path("/{projectId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@PathParam("projectId") long projectId)
			throws JsonGenerationException, JsonMappingException, IOException {
		return new ObjectMapper().writeValueAsString(service.get(projectId));
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String gets(@DefaultValue("0") @QueryParam("start") int start,
			@DefaultValue("5") @QueryParam("size") int size,
			@DefaultValue("") @QueryParam("query") String query)
			throws JsonGenerationException, JsonMappingException, IOException,
			EntityNotFoundException {
		return new ObjectMapper().writeValueAsString(service.gets(start, size,
				query));
	}

	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public String search(@DefaultValue("0") @QueryParam("start") int start,
			@DefaultValue("5") @QueryParam("size") int size,
			@QueryParam("query") String query) throws JsonGenerationException,
			JsonMappingException, IOException {
		return new ObjectMapper().writeValueAsString(service.search(start,
				size, query));
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(Project project) {
		return Response.ok().entity(service.assign(project)).build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{projectId}")
	public Response delete(@PathParam("projectId") long projectId) {
		return Response.ok().entity(service.delete(projectId)).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{projectId}")
	public Response update(Project project,
			@PathParam("projectId") long projectId) {
		return Response.ok().entity(service.update(project, projectId)).build();
	}

	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{projectId}")
	public Response patch(Project project,
			@PathParam("projectId") long projectId) {
		return Response.ok().entity(service.patch(project, projectId)).build();
	}
}