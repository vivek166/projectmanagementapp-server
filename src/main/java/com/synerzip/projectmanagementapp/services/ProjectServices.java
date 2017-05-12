package com.synerzip.projectmanagementapp.services;

import java.util.List;

import com.synerzip.projectmanagementapp.model.Employee;
import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.model.Project;

public interface ProjectServices {

	Project getProject(long projectId);

	PageResult getProjects(int start, int size, String content);

	Project addProject(Project project);

	String deleteProject(long projectId);

	Project updateProject(Project project, long projectId);

	List<Employee> getProjectEmployees(long projectId);

	Project updateProjectPartially(Project project, long projectId);

	PageResult searchProject(int start, int size, String content);
}
