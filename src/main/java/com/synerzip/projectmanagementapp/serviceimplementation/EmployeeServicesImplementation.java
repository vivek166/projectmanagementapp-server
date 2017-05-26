package com.synerzip.projectmanagementapp.serviceimplementation;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import com.mysql.jdbc.StringUtils;
import com.synerzip.projectmanagementapp.dbconnection.HibernateUtils;
import com.synerzip.projectmanagementapp.exception.CanNotEmptyField;
import com.synerzip.projectmanagementapp.exception.MediaTypeException;
import com.synerzip.projectmanagementapp.model.Company;
import com.synerzip.projectmanagementapp.model.Employee;
import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.model.Project;
import com.synerzip.projectmanagementapp.model.ProjectEmployee;
import com.synerzip.projectmanagementapp.services.EmployeeServices;

public class EmployeeServicesImplementation implements EmployeeServices {

	static final Logger logger = Logger
			.getLogger(EmployeeServicesImplementation.class);

	public Employee get(long empId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		Employee employee;
		try {
			employee = (Employee) session.get(Employee.class, empId);
			if (employee == null) {
				logger.error("employee not found  with empid :-" + empId);
				throw new EntityNotFoundException("record not found with id "
						+ empId);
			}
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, get() of employee for empId :-"
					+ empId);
			throw new HibernateException("unable to process your request");
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
		return employee;
	}

	public PageResult gets(int start, int size, String content) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		if (org.apache.commons.lang.StringUtils.isEmpty(content)) {
			try {
				int count = ((Long) session
						.createQuery(
								"select count(*) from com.synerzip.projectmanagementapp.model.Employee")
						.uniqueResult()).intValue();
				if (count > 0) {
					Query query = session
							.createQuery("from com.synerzip.projectmanagementapp.model.Employee");
					query.setFirstResult(start);
					query.setMaxResults(size);
					List<Employee> employees = query.list();
					session.flush();
					PageResult pageResults = new PageResult();
					pageResults.setData(employees);
					pageResults.setTotalResult(count);
					return pageResults;
				} else {
					logger.error("employee not found ");
					throw new EntityNotFoundException("no record found ");
				}
			} catch (Exception exception) {
				logger.error("abnormal ternination, gets() of employee");
				throw new HibernateException("unable to process your request");
			} finally {
				session.close();
				logger.info("session closed successfully");
			}
		} else {
			return search(start, size, content);
		}
	}

	public PageResult search(int start, int size, String content) {
		EntityManager entityManager = Persistence.createEntityManagerFactory(
				"HibernatePersistence").createEntityManager();
		FullTextEntityManager fullTextEntityManager = Search
				.getFullTextEntityManager(entityManager);
		try {
			try {
				fullTextEntityManager.createIndexer().startAndWait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			QueryBuilder qb = fullTextEntityManager.getSearchFactory()
					.buildQueryBuilder().forEntity(Employee.class).get();
			org.apache.lucene.search.Query query = qb
					.keyword()
					.onFields("empId", "empName", "empDepartment",
							"empSubjects").matching(content).createQuery();
			javax.persistence.Query fullTextQuery = fullTextEntityManager
					.createFullTextQuery(query, Employee.class);
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
				logger.error("does not found any matched record with content:-"
						+ content);
				throw new EntityNotFoundException("No record matching with "
						+ content);
			}

		} catch (HibernateException exception) {
			logger.error("abnormal ternination, search() of employee");
			throw new HibernateException("unable to process your request");
		} finally {
			if (fullTextEntityManager != null) {
				fullTextEntityManager.close();
				logger.info("session closed successfully");
			}
			fullTextEntityManager = null;
		}
	}

	public Employee add(Employee employee) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			if (StringUtils.isEmptyOrWhitespaceOnly(employee.getEmpName())) {
				logger.error("employee name is empty");
				throw new CanNotEmptyField("employee name must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(employee
					.getEmpDepartment())) {
				logger.error("employee department is empty");
				throw new CanNotEmptyField("employee department must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(employee
					.getEmpSubjects())) {
				logger.error("employee subject is empty");
				throw new CanNotEmptyField("employee subjects must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(employee
					.getEmployeeType())) {
				logger.error("employee type is empty");
				throw new CanNotEmptyField("employee type must be filled");
			} else {
				session.save(employee);
				tx.commit();
			}
			return employee;
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, add() of employee");
			throw new ConstraintViolationException(
					"record already present with name - "
							+ employee.getEmpName(), null, null);
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
	}

	public String delete(long empId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			String deleteQuery = "DELETE FROM Employee WHERE emp_id = :emp_id";
			Query query = session.createQuery(deleteQuery);
			query.setParameter("emp_id", empId);
			int affectedRow = query.executeUpdate();
			if (affectedRow == 0) {
				logger.error("record already deleted or not exist");
				throw new EntityNotFoundException("record already deleted");
			}
			tx.commit();
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, delete() of employee for empId :-"
					+ empId);
			throw new HibernateException("unable to process your request");
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
		return "record deleted";
	}

	public Employee update(Employee employee, long empId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			if (StringUtils.isEmptyOrWhitespaceOnly(employee.getEmpName())) {
				logger.error("employee name is empty");
				throw new CanNotEmptyField("employee name must be field");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(employee
					.getEmpDepartment())) {
				logger.error("employee department is empty");
				throw new CanNotEmptyField("employee department must be field");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(employee
					.getEmpSubjects())) {
				logger.error("employee subject is empty");
				throw new CanNotEmptyField("employee subjects must be field");
			} else {
				session.saveOrUpdate(employee);
				tx.commit();
			}
			return employee;
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, update() of employee");
			tx.rollback();
			throw new ConstraintViolationException(
					"record already present with name - "
							+ employee.getEmpName(), null, null);
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
	}

	public Employee patch(Employee employee, long empId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			Employee dbProject = (Employee) session.get(Employee.class, empId);
			if (dbProject != null) {
				if (!StringUtils.isEmptyOrWhitespaceOnly(employee.getEmpName())) {
					dbProject.setEmpName(employee.getEmpName());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(employee
						.getEmpDepartment())) {
					dbProject.setEmpDepartment(employee.getEmpDepartment());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(employee
						.getEmpSubjects())) {
					dbProject.setEmpSubjects(employee.getEmpSubjects());
				}
				session.save(dbProject);
				session.flush();
				tx.commit();
			} else {
				logger.error("Can't update, employee not found with empId :-"
						+ empId);
				throw new EntityNotFoundException(
						"Can't update, employee not found with empId :-"
								+ empId);
			}
			return dbProject;
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, patch() of employee");
			throw new ConstraintViolationException(
					"unable to update, record already present with empName :-"
							+ employee.getEmpName(), null, null);
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
	}

	public List<Project> assigned(long empId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		List<Project> projectResult = null;
		try {
			Query query = session
					.createQuery("select project from ProjectEmployee where emp_id = :emp_id");
			query.setParameter("emp_id", empId);
			projectResult = query.list();
			if (projectResult.size() == 0) {
				logger.error("No Project assign to this employee " + empId);
				throw new EntityNotFoundException(
						"No Project assign to this employee " + empId);
			}
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, assigned() of employee");
			throw new HibernateException("unable to process your request");
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
		return projectResult;
	}

	public ProjectEmployee assign(Employee employee) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction txEmployee = session.beginTransaction();
		try {
			session.save(employee);
			txEmployee.commit();
			if (employee.getEmployeeType().equals("employee")) {
				List<Integer> projectIds = employee.getProjectIds();
				ProjectEmployee projectEmployee = new ProjectEmployee();
				for (Integer projectId : projectIds) {
					Project project = (Project) session.get(Project.class,
							(long) projectId);
					if (project != null) {
						org.hibernate.Transaction txProject = session
								.beginTransaction();
						projectEmployee.setEmployee(employee);
						projectEmployee.setProject(project);
						session.save(projectEmployee);
						txProject.commit();
					} else {
						logger.error("project does not exist with id "
								+ projectId);
						throw new EntityNotFoundException(
								"unable to  assign employee, project does not exist with id :-"
										+ projectId);
					}
				}
				return projectEmployee;
			} else {
				logger.error("employee must be of employee type but It is :-"
						+ employee.getEmployeeType() + "type");
				throw new MediaTypeException(
						"can't assign project, employee must be of employee type");
			}
		} catch (HibernateException exception) {
			logger.error("trying to insert duplicate value");
			throw new ConstraintViolationException(
					"unable to assign, emp already present with empName :-"
							+ employee.getEmpName(), null, null);
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
	}

}