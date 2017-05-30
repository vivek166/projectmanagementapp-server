package com.synerzip.projectmanagementapp.services;

import java.util.List;
import com.synerzip.projectmanagementapp.model.Employee;
import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.model.Project;
import com.synerzip.projectmanagementapp.model.ProjectEmployee;

public interface EmployeeServices {

	Employee get(long id);

	PageResult gets(int start, int size, String content);

	Employee add(Employee employee);

	String delete(long id);

	Employee update(Employee employee, long id);

	List<Project> assigned(long id);

	ProjectEmployee assign(Employee employee);

	PageResult search(int start, int size, String content);

	Employee patch(Employee employee, long id);
}