package com.synerzip.projectmanagementapp.services;

import java.util.List;
import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.model.Project;
import com.synerzip.projectmanagementapp.model.ProjectEmployee;
import com.synerzip.projectmanagementapp.model.User;

public interface ProjectServices {

	Project get(long projectId);

	PageResult gets(int start, int size, String content);

	Project add(Project project);

	String delete(long projectId);

	Project update(Project project, long projectId);

	List<User> assigned(long projectId);

	ProjectEmployee assign(Project project);

	Project patch(Project project, long projectId);

	PageResult search(int start, int size, String content);
	
	List<Project> getProjects();
	
}