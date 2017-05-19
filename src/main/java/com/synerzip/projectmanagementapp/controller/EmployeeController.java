package com.synerzip.projectmanagementapp.controller;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityNotFoundException;
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

import com.synerzip.projectmanagementapp.exception.DataNotFoundException;
import com.synerzip.projectmanagementapp.model.Employee;
import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.serviceimplementation.EmployeeServicesImplementation;

@Path("/employee")
public class EmployeeController {

	EmployeeServicesImplementation service = new EmployeeServicesImplementation();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{empId}")
	public Response get(@PathParam("empId") long empId) {
		Employee employee = service.get(empId);
		if (employee == null) {
			throw new EntityNotFoundException("no such record found");
		}
		return Response.ok().entity(employee).build();
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
	public Employee add(Employee employee) {
		return service.add(employee);
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{empId}")
	public String delete(@PathParam("empId") long empId) {
		return service.delete(empId);
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{empId}")
	public Employee update(Employee employee, @PathParam("empId") long empId) {
		return service.update(employee, empId);
	}
}