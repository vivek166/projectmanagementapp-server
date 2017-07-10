package com.synerzip.projectmanagementapp.services;

import java.util.List;
import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.model.Project;
import com.synerzip.projectmanagementapp.model.ProjectEmployee;
import com.synerzip.projectmanagementapp.model.User;

public interface ProjectServices {

	Project get(long projectId, long companyId);

	PageResult gets(int start, int size, String content, long companyId);

	Project add(Project project);

	String delete(long projectId, long companyId);

	Project update(Project project, long projectId);

	List<User> assigned(long projectId);

	ProjectEmployee assign(Project project);

	Project patch(Project project, long projectId);

	PageResult search(int start, int size, String content, long companyId);

	List<Object> getProjects(int start, int size, String content, long userId, long companyId);

}