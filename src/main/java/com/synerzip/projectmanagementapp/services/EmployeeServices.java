package com.synerzip.projectmanagementapp.services;

import java.util.List;


import com.synerzip.projectmanagementapp.model.Employee;

public interface EmployeeServices {

	Employee getEmployee(long empId);

	List<Employee> getEmployees(int start, int size);

	Employee addEmployee(Employee employee);

	String deleteEmployee(long empId);

	Employee updateEmployee(Employee employee, long empId);
	
	List<Employee> searchEmployee(String content);
}
