package com.synerzip.projectmanagementapp.serviceimplementation;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
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

	public Employee get(long empId) {
		Session session = HibernateUtils.getSession();
		Employee employee;
		try {
			employee = (Employee) session.get(Employee.class, empId);
			employee.setProjects(null);
			if (employee == null) {
				throw new EntityNotFoundException("record not found with id " + empId);
			}
		} catch (HibernateException e) {
			throw new HibernateException("database error");
		} finally {
			session.close();
		}
		return employee;
	}

	public PageResult gets(int start, int size, String content) {
		Session session = HibernateUtils.getSession();
		if (org.apache.commons.lang.StringUtils.isEmpty(content)) {
			try {
				int count = ((Long) session
						.createQuery("select count(*) from com.synerzip.projectmanagementapp.model.Employee")
						.uniqueResult()).intValue();
				if (count > 0) {
					Query query = session.createQuery("from com.synerzip.projectmanagementapp.model.Employee");
					query.setFirstResult(start);
					query.setMaxResults(size);
					List<Employee> employees = query.list();
					session.flush();
					PageResult pageResults = new PageResult();
					pageResults.setData(employees);
					pageResults.setTotalResult(count);
					return pageResults;
				} else {
					throw new EntityNotFoundException("no record found ");
				}
			} catch (Exception e) {
				throw new EntityNotFoundException("database error");
			} finally {
				session.close();
			}
		} else {
			return search(start, size, content);
		}
	}

	public PageResult search(int start, int size, String content) {
		EntityManager entityManager = Persistence.createEntityManagerFactory("HibernatePersistence")
				.createEntityManager();
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		try {
			// fullTextEntityManager.createIndexer().startAndWait();
			QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Employee.class)
					.get();
			org.apache.lucene.search.Query query = qb.keyword()
					.onFields("empId", "empName", "empDepartment", "empSubjects").matching(content).createQuery();
			javax.persistence.Query fullTextQuery = fullTextEntityManager.createFullTextQuery(query, Employee.class);
			fullTextQuery.setFirstResult(start);
			fullTextQuery.setMaxResults(size);
			int count = fullTextQuery.getResultList().size();
			List<Employee> employeeResult = fullTextQuery.getResultList();
			if (employeeResult.size() != 0) {
				PageResult pageResults = new PageResult();
				pageResults.setData(employeeResult);
				pageResults.setTotalResult(count);
				return pageResults;
			} else {
				throw new EntityNotFoundException("No record matching with " + content);
			}

		} catch (Exception e) {
			throw new EntityNotFoundException("No record matching with " + content);
		} finally {
			if (fullTextEntityManager != null) {
				fullTextEntityManager.close();
			}
			fullTextEntityManager = null;
		}
	}

	public Employee add(Employee employee) {

		Session session = HibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			session.save(employee);
			// addEmployeeProject(employee);
			tx.commit();
			return employee;
		} catch (Exception e) {
			throw new ConstraintViolationException("record already present with name-- "+employee.getEmpName(), null, null);
		} finally {
			session.close();
		}
	}

	public String delete(long empId) {
		Session session = HibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			String deleteQuery = "DELETE FROM Employee WHERE emp_id = :emp_id";
			Query query = session.createQuery(deleteQuery);
			query.setParameter("emp_id", empId);
			int affectedRow = query.executeUpdate();
			tx.commit();
			if (affectedRow == 0) {
				throw new ObjectNotFoundException("record already deleted", deleteQuery);
			}
		} catch (Exception e) {
			throw new ObjectNotFoundException(e, "database error");
		} finally {
			session.close();
		}
		return "record deleted";
	}

	public Employee update(Employee employee, long empId) {
		Session session = HibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			session.saveOrUpdate(employee);
			tx.commit();
			return employee;
		} catch (Exception e) {
			tx.rollback();
			throw new HibernateException("record already with same employee name");
		} finally {
			session.close();
		}
	}

	public List<Project> assigned(long empId) {
		Session session = HibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		List<Project> projectResult=null;
		try {
			Query query = session.createQuery("select project from ProjectEmployee where emp_id = :emp_id");
			query.setParameter("emp_id", empId);
			projectResult=query.list();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
		return projectResult;
	}

	public ProjectEmployee assign(Employee employee) {
		Session session = HibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		employee.setProjects(null);
		session.save(employee);
		List<Integer> projectIds = employee.getProjectIds();
		ProjectEmployee projectEmployee =new ProjectEmployee();
		for (Integer empId : projectIds) {
			try {
				Project project = (Project) session.get(Project.class, (long) empId);
				if(project!=null){
				projectEmployee.setEmployee(employee);
				projectEmployee.setProject(project);
				session.save(projectEmployee);
				tx.commit();
				}else{
					throw new HibernateException("can't assign emp not exist");
				}
			} catch (Exception exception) {
				throw new HibernateException("can't assign emp not exist");
			} finally {
				session.close();
			}
		}
		return projectEmployee;
	}
}