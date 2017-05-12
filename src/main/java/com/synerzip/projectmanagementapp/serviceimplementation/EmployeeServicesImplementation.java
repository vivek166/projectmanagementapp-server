package com.synerzip.projectmanagementapp.serviceimplementation;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import com.synerzip.projectmanagementapp.dbconnection.HibernateUtils;
import com.synerzip.projectmanagementapp.model.Employee;
import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.model.Project;
import com.synerzip.projectmanagementapp.model.ProjectEmployee;
import com.synerzip.projectmanagementapp.services.EmployeeServices;

public class EmployeeServicesImplementation implements EmployeeServices {

	public Employee getEmployee(long empId) {

		Session session = HibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		Employee employee = new Employee();
		try {
			employee = (Employee) session.get(Employee.class, empId);
			//employee.setProjectEmployees(null);
			tx.commit();
			return employee;
		} catch (Exception e) {
			return null;
		} finally {
			session.close();
		}
	}

	public PageResult getEmployees(int start, int size, String content) {
		Session session = HibernateUtils.getSession();
		session.beginTransaction();
		if (content.length() == 0) {
			try {
				Query query = session.createQuery("from com.synerzip.projectmanagementapp.model.Employee");
				query.setFirstResult(start);
				query.setMaxResults(size);
				List<Employee> employees = query.list();
				int count = ((Long)session.createQuery("select count(*) from com.synerzip.projectmanagementapp.model.Employee").uniqueResult()).intValue();
				session.flush();
				session.getTransaction().commit();
				PageResult pageResults = new PageResult();
				pageResults.setData(employees);
				pageResults.setTotalResult(count);
				return pageResults;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return searchEmployee(start, size, content);
		}
		session.getTransaction().commit();
		return null;
	}

	public PageResult searchEmployee(int start, int size, String content) {
		EntityManager entityManager = Persistence.createEntityManagerFactory("MumzHibernateSearch")
				.createEntityManager();
		entityManager.getTransaction().begin();
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		try {
			// fullTextEntityManager.createIndexer().startAndWait();
			QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Employee.class)
					.get();
			org.apache.lucene.search.Query query = qb.keyword()
					.onFields("empId", "empName", "empDepartment", "empSubjects").matching(content).createQuery();
			javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(query, Employee.class);
			jpaQuery.setFirstResult(start);
			jpaQuery.setMaxResults(size);
			List<Employee> employeeResult = jpaQuery.getResultList();
			if (employeeResult != null) {
				PageResult pageResults = new PageResult();
				pageResults.setData(employeeResult);
				pageResults.setTotalResult(0);
				return pageResults;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fullTextEntityManager != null) {
				fullTextEntityManager.close();
			}
			fullTextEntityManager = null;
		}
		entityManager.getTransaction().commit();
		return null;
	}

	public Employee addEmployee(Employee employee) {

		Session session = HibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			session.save(employee);
			// addEmployeeProject(employee);
			tx.commit();
			return employee;
		} catch (Exception e) {
			return null;
		} finally {
			session.close();
		}
	}

	private void addEmployeeProject(Employee employee) {
		Session session = HibernateUtils.getSession();
		Session sessionPE = HibernateUtils.getSession();
		org.hibernate.Transaction tx = sessionPE.beginTransaction();
		List<Integer> projectIds = employee.getProjectIds();
		for (Integer projectId : projectIds) {
			try {
				Project project = (Project) session.get(Project.class, (long) projectId);
				ProjectEmployee pe = new ProjectEmployee();
				pe.setEmployee(employee);
				pe.setProject(project);
				sessionPE.save(pe);
				sessionPE.flush();
				tx.commit();
			} catch (Exception exception) {
				exception.printStackTrace();
			} finally {
				sessionPE.close();
			}
		}
	}

	public String deleteEmployee(long empId) {

		Session session = HibernateUtils.getSession();
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
		Session session = HibernateUtils.getSession();
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
