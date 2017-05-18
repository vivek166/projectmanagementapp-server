package com.synerzip.projectmanagementapp.services;

import java.util.List;

import com.synerzip.projectmanagementapp.model.Employee;
import com.synerzip.projectmanagementapp.model.PageResult;

public interface EmployeeServices {

	Employee get(long empId);

	PageResult gets(int start, int size, String content);

	Employee add(Employee employee);

	String delete(long empId);

	Employee update(Employee employee, long empId);

	PageResult search(int start, int size, String content);
}
