package com.synerzip.projectmanagementapp.services;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.hibernate.HibernateException;

import com.synerzip.projectmanagementapp.model.Employee;
import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.model.Project;

public interface ProjectServices {

	Project get(long projectId);

	PageResult gets(int start, int size, String content);

	Project add(Project project);

	String delete(long projectId);

	Project update(Project project, long projectId);

	List<Employee> getProjectEmployees(long projectId);

	Project patch(Project project, long projectId);

	PageResult search(int start, int size, String content);
}
