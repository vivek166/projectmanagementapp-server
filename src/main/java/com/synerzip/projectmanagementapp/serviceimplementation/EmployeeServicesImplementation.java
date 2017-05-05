package com.synerzip.projectmanagementapp.serviceimplementation;

import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

import com.synerzip.projectmanagementapp.dbconnection.EmployeeHibernateUtils;
import com.synerzip.projectmanagementapp.model.Employee;
import com.synerzip.projectmanagementapp.model.Project;
import com.synerzip.projectmanagementapp.model.Project_Employee;
import com.synerzip.projectmanagementapp.services.EmployeeServices;

public class EmployeeServicesImplementation implements EmployeeServices {

	public Employee getEmployee(long empId) {

		Session session = EmployeeHibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		Employee employee=new Employee();
		try {
			employee = (Employee) session.get(Employee.class, empId);
			employee.setProject_employees(null);
			tx.commit();
			return employee;
		} catch (Exception e) {
			return null;
		} finally {
			session.close();
		}
	}

	public List<Employee> getEmployees(int start, int size) {
		Session session = EmployeeHibernateUtils.getSession();
		session.beginTransaction();
		try {
			Query query = session
					.createQuery("from com.synerzip.projectmanagementapp.model.Employee");
			query.setFirstResult(start);
			query.setMaxResults(size);
			List<Employee> employees = query.list();
			return employees;
		} catch (Exception e) {
			return null;
		}finally {
			session.close();
		}
	}

	public Employee addEmployee(Employee employee) {

		Session session = EmployeeHibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		
		try {
			session.save(employee);
			addEmployeeProject(employee);
			tx.commit();
			return employee;
		} catch (Exception e) {
			return null;
		} finally {
			session.close();
		}
	}

	private void addEmployeeProject(Employee employee) {
		Session session = EmployeeHibernateUtils.getSession();
		Session sessionPE = EmployeeHibernateUtils.getSession();
		org.hibernate.Transaction tx = sessionPE.beginTransaction();
		List<Integer> projectIds=employee.getProject_id();
		for(Integer projectId : projectIds){
			Project project=(Project)session.get(Project.class, projectId);
			Project_Employee pe=new Project_Employee();
			pe.setEmployee(employee);
			pe.setProject(project);
			sessionPE.save(pe);
			sessionPE.flush();
			tx.commit();
		}
	}

	public String deleteEmployee(long empId) {

		Session session = EmployeeHibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();

		try {
			String deleteQuery = "DELETE FROM Employee WHERE emp_id = :emp_id";
			Query query = session.createQuery(deleteQuery);
			query.setParameter("emp_id", empId);
			query.executeUpdate();
			tx.commit();
		} catch (Exception e) {
			return null;
		} finally {
			session.close();
		}
		return "record deleted";
	}

	public Employee updateEmployee(Employee employee, long empId) {
		Session session = EmployeeHibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();

		try {
			String deleteQuery = "DELETE FROM Employee WHERE emp_id = :emp_id";
			Query query = session.createQuery(deleteQuery);
			query.setParameter("emp_id", empId);
			query.executeUpdate();
			session.save(employee);
			tx.commit();
			return employee;
		} catch (Exception e) {
			return null;
		} finally {
			session.close();
		}
	}
}
