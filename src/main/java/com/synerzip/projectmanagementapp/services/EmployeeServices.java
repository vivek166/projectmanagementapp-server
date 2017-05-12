package com.synerzip.projectmanagementapp.services;

import java.util.List;

import com.synerzip.projectmanagementapp.model.Employee;
import com.synerzip.projectmanagementapp.model.PageResult;

public interface EmployeeServices {

	Employee getEmployee(long empId);

	PageResult getEmployees(int start, int size, String content);

	Employee addEmployee(Employee employee);

	String deleteEmployee(long empId);

	Employee updateEmployee(Employee employee, long empId);

	PageResult searchEmployee(int start, int size, String content);
}
