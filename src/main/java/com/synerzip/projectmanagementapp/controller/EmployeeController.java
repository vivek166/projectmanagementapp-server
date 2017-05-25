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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import com.synerzip.projectmanagementapp.httpmethods.Patch.PATCH;
import com.synerzip.projectmanagementapp.model.Employee;
import com.synerzip.projectmanagementapp.serviceimplementation.EmployeeServicesImplementation;

@Path("/employee")
public class EmployeeController {

	EmployeeServicesImplementation service = new EmployeeServicesImplementation();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{empId}")
	public String get(@PathParam("empId") long empId)
			throws JsonGenerationException, JsonMappingException, IOException {
		return new ObjectMapper().writeValueAsString(service.get(empId));
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String gets(@DefaultValue("0") @QueryParam("start") int start,
			@DefaultValue("5") @QueryParam("size") int size, @DefaultValue("") @QueryParam("query") String query)
			throws JsonGenerationException, JsonMappingException, IOException {
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
	public Response add(Employee employee) {
		return Response.ok().entity(service.add(employee)).build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{empId}")
	public Response delete(@PathParam("empId") long empId) {
		return Response.ok().entity(service.delete(empId)).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{empId}")
	public Response update(Employee employee, @PathParam("empId") long empId) {
		return Response.ok().entity(service.update(employee, empId)).build();
	}

	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{empId}")
	public Response patch(Employee employee, @PathParam("empId") long empId) {
		return Response.ok().entity(service.patch(employee, empId)).build();
	}
}