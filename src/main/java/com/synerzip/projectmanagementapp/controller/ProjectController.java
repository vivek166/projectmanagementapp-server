package com.synerzip.projectmanagementapp.controller;

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.synerzip.projectmanagementapp.httpmethods.Patch.PATCH;
import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.model.Project;
import com.synerzip.projectmanagementapp.serviceimplementation.ProjectServiceImplementation;

@Path("/project")
public class ProjectController {

	ProjectServiceImplementation service = new ProjectServiceImplementation();

	@GET@Produces(MediaType.APPLICATION_JSON)@Path("/{projectId}")
	public Project getProject(@PathParam("projectId") long projectId) {
		return service.getProject(projectId);
	}

	@GET@Produces(MediaType.APPLICATION_JSON)
	public String getProjects(@DefaultValue("0")@QueryParam("start") int start, @DefaultValue("5")@QueryParam("size") int size, @DefaultValue("")@QueryParam("query") String query)
	throws JsonGenerationException,
	JsonMappingException,
	IOException {

		return new ObjectMapper().writeValueAsString(service.getProjects(start, size, query));
	}

	@GET@Path("/search")@Produces(MediaType.APPLICATION_JSON)
	public String searchProject(@DefaultValue("0")@QueryParam("start") int start, @DefaultValue("5")@QueryParam("size") int size, @QueryParam("query") String query) throws JsonGenerationException,
	JsonMappingException,
	IOException {
		return new ObjectMapper().writeValueAsString(service.searchProject(start, size, query));
	}

	@POST@Consumes(MediaType.APPLICATION_JSON)@Produces(MediaType.APPLICATION_JSON)
	public Project addProject(Project project) {
		return service.addProject(project);
	}

	@DELETE@Produces(MediaType.APPLICATION_JSON)@Path("{projectId}")
	public String deleteProject(@PathParam("projectId") long projectId) {
		return service.deleteProject(projectId);
	}

	@PUT@Consumes(MediaType.APPLICATION_JSON)@Produces(MediaType.APPLICATION_JSON)@Path("{projectId}")
	public Project updateProject(Project project, @PathParam("projectId") long projectId) {
		return service.updateProject(project, projectId);
	}

	@PATCH@Consumes(MediaType.APPLICATION_JSON)@Produces(MediaType.APPLICATION_JSON)@Path("{projectId}")
	public Project updateProjectPartially(Project project, @PathParam("projectId") long projectId) {
		return service.updateProjectPartially(project, projectId);
	}
}