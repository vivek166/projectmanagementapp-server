package com.synerzip.projectmanagementapp.services;

import java.util.List;
import com.synerzip.projectmanagementapp.model.Project;

public interface ProjectServices {

	Project getProject(long projectId);

	List<Project> getProjects(int start, int size);
	
	Project addProject(Project project);

	String deleteProject(long projectId);

	Project updateProject(Project project, long projectId);

}
