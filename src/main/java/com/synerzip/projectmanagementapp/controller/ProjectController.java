package com.synerzip.projectmanagementapp.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.synerzip.projectmanagementapp.model.Project;
import com.synerzip.projectmanagementapp.serviceimplementation.ProjectServiceImplementation;

@Path("/project")
public class ProjectController {

	ProjectServiceImplementation service = new ProjectServiceImplementation();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{projectId}")
	public Project getProject(@PathParam("projectId") long projectId) {
		return service.getProject(projectId);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Project> getProjects(
			@DefaultValue("1") @QueryParam("start") int start,
			@DefaultValue("5") @QueryParam("size") int size) {
		return service.getProjects(start, size);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Project addProject(Project project) {
		return service.addProject(project);
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{projectId}")
	public String deleteProject(@PathParam("projectId") long projectId) {
		return service.deleteProject(projectId);
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{projectId}")
	public Project updateProject(Project project,
			@PathParam("projectId") long projectId) {
		return service.updateProject(project, projectId);
	}
}
