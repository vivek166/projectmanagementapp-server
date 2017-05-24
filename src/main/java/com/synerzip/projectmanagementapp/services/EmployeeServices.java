package com.synerzip.projectmanagementapp.services;

import java.util.List;

import com.synerzip.projectmanagementapp.model.Employee;
import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.model.Project;
import com.synerzip.projectmanagementapp.model.ProjectEmployee;

public interface EmployeeServices {

	Employee get(long empId);

	PageResult gets(int start, int size, String content);

	Employee add(Employee employee);

	String delete(long empId);

	Employee update(Employee employee, long empId);

	List<Project> assigned(long empId);

	ProjectEmployee assign(Employee employee);

	PageResult search(int start, int size, String content);

	Employee patch(Employee employee, long empId);
}