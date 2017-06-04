package com.synerzip.projectmanagementapp.serviceimplementation;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.ws.rs.NotAuthorizedException;

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
import com.synerzip.projectmanagementapp.model.Token;
import com.synerzip.projectmanagementapp.model.User;
import com.synerzip.projectmanagementapp.model.UserCredentials;
import com.synerzip.projectmanagementapp.services.EmployeeServices;

public class EmployeeServicesImplementation implements EmployeeServices {

	static final Logger logger = Logger.getLogger(EmployeeServicesImplementation.class);

	public Employee get(long id) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		Employee employee;
		try {
			Query query = session.getNamedQuery("getById");  
		    query.setLong("id", id);  
		    employee= (Employee) query.uniqueResult();  
			if (employee == null) {
				logger.error("employee not found  with empid :-" + id);
				throw new EntityNotFoundException("record not found with id "
						+ id);
			}
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, get() of employee for id :-"
					+ id);
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
					.onFields("firstName", "lastName", "department",
							"subjects").matching(content).createQuery();
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
		String companyName = employee.getCompanyName();
		Company company=new Company();
		company.setCompanyName(companyName);
		CompanyServiceImplementation companyService=new CompanyServiceImplementation();
		company=companyService.add(company);
		try {
			if (StringUtils.isEmptyOrWhitespaceOnly(employee.getFirstName())) {
				logger.error("first name is empty");
				throw new CanNotEmptyField("first name must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(employee.getLastName())) {
				logger.error("last name is empty");
				throw new CanNotEmptyField("last name must be filled");
			}else if (StringUtils.isEmptyOrWhitespaceOnly(employee
					.getDepartment())) {
				logger.error("department is empty");
				throw new CanNotEmptyField("department must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(employee
					.getSkills())) {
				logger.error("skills is empty");
				throw new CanNotEmptyField("skills must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(employee
					.getType())) {
				logger.error("user type is empty");
				throw new CanNotEmptyField("user type must be filled");
			} else {
				employee.setCompany(company);
				session.save(employee);
				tx.commit();
			}
			return employee;
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, add() of employee");
			throw new ConstraintViolationException(
					"record already present with email - "
							+ employee.getEmail(), null, null);
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
	}

	public String delete(long id) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			String deleteQuery = "DELETE FROM Employee WHERE id = :id";
			Query query = session.createQuery(deleteQuery);
			query.setParameter("id", id);
			int affectedRow = query.executeUpdate();
			if (affectedRow == 0) {
				logger.error("record already deleted or not exist");
				throw new EntityNotFoundException("record already deleted");
			}
			tx.commit();
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, delete() of employee for Id :-"
					+ id);
			throw new HibernateException("unable to process your request");
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
		return "record deleted";
	}

	public Employee update(Employee employee, long id) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			if (StringUtils.isEmptyOrWhitespaceOnly(employee.getFirstName())) {
				logger.error("first name is empty");
				throw new CanNotEmptyField("first  name must be field");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(employee
					.getDepartment())) {
				logger.error("department is empty");
				throw new CanNotEmptyField("department must be field");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(employee
					.getSkills())) {
				logger.error("skills is empty");
				throw new CanNotEmptyField("skills must be field");
			} else {
				session.saveOrUpdate(employee);
				tx.commit();
			}
			return employee;
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, update() of employee");
			tx.rollback();
			throw new ConstraintViolationException(
					"record already present with email - "
							+ employee.getEmail(), null, null);
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
				if (!StringUtils.isEmptyOrWhitespaceOnly(employee.getFirstName())) {
					dbProject.setFirstName(employee.getFirstName());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(employee
						.getDepartment())) {
					dbProject.setDepartment(employee.getDepartment());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(employee
						.getSkills())) {
					dbProject.setSkills(employee.getSkills());
				}
				session.save(dbProject);
				session.flush();
				tx.commit();
			} else {
				logger.error("Can't update, employee not found with Id :-"
						+ empId);
				throw new EntityNotFoundException(
						"Can't update, employee not found with Id :-"
								+ empId);
			}
			return dbProject;
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, patch() of employee");
			throw new ConstraintViolationException(
					"unable to update, record already present with email :-"
							+ employee.getEmail(), null, null);
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
	}

	public List<Project> assigned(long id) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		List<Project> projectResult = null;
		try {
			Query query = session
					.createQuery("select project from ProjectEmployee where id = :id");
			query.setParameter("id", id);
			projectResult = query.list();
			if (projectResult.size() == 0) {
				logger.error("No Project assign to this employee " + id);
				throw new EntityNotFoundException(
						"No Project assign to this employee " + id);
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
			if (employee.getType().equals("employee")) {
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
						+ employee.getType() + "type");
				throw new MediaTypeException(
						"can't assign project, employee must be of employee type");
			}
		} catch (HibernateException exception) {
			logger.error("trying to insert duplicate value");
			throw new ConstraintViolationException(
					"unable to assign, emp already present with email :-"
							+ employee.getEmail(), null, null);
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
	}
	
	public String userAuthentication(UserCredentials userCredentials) {
		String userName=userCredentials.getUserName();
		String userPassword=userCredentials.getUserPassword();
		Session session = HibernateUtils.getSession();                             
		String tokenString="";
		try{
			Query query=session.createQuery("from Employee  where email = :email and password = :password");
			query.setParameter("email", userName);
			query.setParameter("password", userPassword);
			List<Employee> dbUser=query.list();
			if(!dbUser.isEmpty()){
				
					tokenString = UUID.randomUUID().toString();
					
					Token token=new Token();
					token.setToken(tokenString);
					token.setUserName(userName);
					token.setExpiryTime(Calendar.getInstance().getTime());
					session.save(token);
					session.beginTransaction().commit();
					return "Bearer "+tokenString;
			}else{
				throw new EntityNotFoundException("userName or password invalid");
			}
		}catch(HibernateException exception){
			throw new EntityNotFoundException("unable to generate token");
		}finally {
			session.close();
		}
		
	}

}