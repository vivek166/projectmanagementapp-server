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

import com.synerzip.projectmanagementapp.authentication.Secure;
import com.synerzip.projectmanagementapp.httpmethods.Patch.PATCH;
import com.synerzip.projectmanagementapp.model.Company;
import com.synerzip.projectmanagementapp.model.User;
import com.synerzip.projectmanagementapp.serviceimplementation.CompanyServiceImplementation;

@Path("/company")
public class CompanyController {

	CompanyServiceImplementation service = new CompanyServiceImplementation();

	@GET
	@Secure
	@Path("/{companyId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@PathParam("companyId") long companyId)
			throws JsonGenerationException, JsonMappingException, IOException {
		return new ObjectMapper().writeValueAsString(service.get(companyId));
	}

	@GET
	@Secure
	@Produces(MediaType.APPLICATION_JSON)
	public String gets(@DefaultValue("0") @QueryParam("start") int start,
			@DefaultValue("5") @QueryParam("size") int size, @QueryParam("companyid") int companyId,
			@DefaultValue("") @QueryParam("query") String query)
			throws JsonGenerationException, JsonMappingException, IOException, EntityNotFoundException {
		return new ObjectMapper().writeValueAsString(service.gets(start, size, companyId, query));
	}

	@GET
	@Secure
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
	@Secure
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{companyId}")
	public Response delete(@PathParam("companyId") long companyId) {
		return Response.ok().entity(service.delete(companyId)).build();
	}

	@PUT
	@Secure
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{companyId}")
	public Response update(Company company, @PathParam("companyId") long companyId) {
		return Response.ok().entity(service.update(company, companyId)).build();
	}

	@PATCH
	@Secure
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{projectId}")
	public Response patch(Company company, @PathParam("projectId") long companyId) {
		return Response.ok().entity(service.patch(company, companyId)).build();
	}
}