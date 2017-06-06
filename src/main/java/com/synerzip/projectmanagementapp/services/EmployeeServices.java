package com.synerzip.projectmanagementapp.services;

import java.util.List;

import javax.ws.rs.core.SecurityContext;
import com.synerzip.projectmanagementapp.model.Employee;
import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.model.Project;
import com.synerzip.projectmanagementapp.model.ProjectEmployee;
import com.synerzip.projectmanagementapp.model.Token;
import com.synerzip.projectmanagementapp.model.UserCredentials;

public interface EmployeeServices {

	Employee get(long id, SecurityContext securityContext);

	PageResult gets(int start, int size, String content);

	Employee add(Employee employee);

	String delete(long id);

	Employee update(Employee employee, long id);

	List<Project> assigned(long id);

	ProjectEmployee assign(Employee employee);

	PageResult search(int start, int size, String content);

	Employee patch(Employee employee, long id);
	
    Token userAuthentication(UserCredentials userCredentials);
}