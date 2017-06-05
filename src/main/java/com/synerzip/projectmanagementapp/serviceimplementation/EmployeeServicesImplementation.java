package com.synerzip.projectmanagementapp.serviceimplementation;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
<<<<<<< HEAD
import java.util.UUID;

=======
>>>>>>> 6ca684102c93a879cbab27e7bba963124af17a3d
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
import com.synerzip.projectmanagementapp.exception.CanNotEmptyFilled;
import com.synerzip.projectmanagementapp.exception.MediaTypeException;
import com.synerzip.projectmanagementapp.model.Company;
import com.synerzip.projectmanagementapp.model.Employee;
import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.model.Project;
import com.synerzip.projectmanagementapp.model.ProjectEmployee;
import com.synerzip.projectmanagementapp.model.Token;
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
			employee = (Employee) query.uniqueResult();
			if (employee == null) {
				logger.error("employee not found  with empid :-" + id);
				throw new EntityNotFoundException("record not found with id " + id);
			}
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, get() of employee for id :-" + id);
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
		EntityManager entityManager = Persistence.createEntityManagerFactory("HibernatePersistence")
				.createEntityManager();
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		try {
			
			  /*try { fullTextEntityManager.createIndexer().startAndWait(); }
			  catch (InterruptedException e) { e.printStackTrace(); }*/
			 
			QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Employee.class)
					.get();
			org.apache.lucene.search.Query query = qb.keyword()
					.onFields("firstName", "lastName", "department", "skills", "email").matching(content)
					.createQuery();
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
				logger.error("does not found any matched record with content:-" + content);
				throw new EntityNotFoundException("No record matching with " + content);
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

	@SuppressWarnings("null")
	public Employee add(Employee employee) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		String companyName = employee.getCompanyName();
		try {
			Company company = isRegisteredComapany(companyName);
			if (company != null) {
				employee.setCompany(company);
			} else {
				company.setCompanyName(companyName);
				CompanyServiceImplementation companyService = new CompanyServiceImplementation();
				company = companyService.add(company);
			}

			if (StringUtils.isEmptyOrWhitespaceOnly(employee.getFirstName())) {
				logger.error("first name is empty");
				throw new CanNotEmptyFilled("first name must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(employee.getLastName())) {
				logger.error("last name is empty");
				throw new CanNotEmptyFilled("last name must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(employee.getDepartment())) {
				logger.error("department is empty");
				throw new CanNotEmptyFilled("department must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(employee.getSkills())) {
				logger.error("skills is empty");
				throw new CanNotEmptyFilled("skills must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(employee.getType())) {
				logger.error("user type is empty");
				throw new CanNotEmptyFilled("user type must be filled");
			} else {
				employee.setCompany(company);
				session.save(employee);
				tx.commit();
			}
			return employee;
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, add() of employee");
			throw new ConstraintViolationException("record already present with email - " + employee.getEmail(), null,
					null);
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
			logger.error("abnormal ternination, delete() of employee for Id :-" + id);
			throw new HibernateException("unable to process your request");
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
		return "record deleted";
	}

	@SuppressWarnings("null")
	public Employee update(Employee employee, long id) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			/*
			 * if (StringUtils.isEmptyOrWhitespaceOnly(employee.getFirstName()))
			 * { logger.error("first name is empty"); throw new
			 * CanNotEmptyfilled("first  name must be filled"); } else if
			 * (StringUtils.isEmptyOrWhitespaceOnly(employee .getDepartment()))
			 * { logger.error("department is empty"); throw new
			 * CanNotEmptyfilled("department must be filled"); } else if
			 * (StringUtils.isEmptyOrWhitespaceOnly(employee .getSkills())) {
			 * logger.error("skills is empty"); throw new CanNotEmptyfilled(
			 * "skills must be filled"); } else { session.saveOrUpdate(employee);
			 * tx.commit(); }
			 */
			if (!StringUtils.isEmptyOrWhitespaceOnly(employee.getCompanyName())) {
				Company company=isRegisteredComapany(employee.getCompanyName());
				if(company!=null){
					employee.setCompany(company);
				}else{
					CompanyServiceImplementation companyService = new CompanyServiceImplementation();
					Company newCompany=new Company();
					newCompany.setCompanyName(employee.getCompanyName());
					employee.setCompany(companyService.add(newCompany));
				}
			    
			} else{
				logger.error("company name is empty"); 
				throw new CanNotEmptyFilled("company  name must be filled"); 
			}
			session.saveOrUpdate(employee);
			tx.commit();
			return employee;
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, update() of employee");
			tx.rollback();
			throw new ConstraintViolationException("record already present with email - " + employee.getEmail(), null,
					null);
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
	}

	public Employee patch(Employee employee, long id) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			Employee dbEmployee = (Employee) session.get(Employee.class, id);
			if (dbEmployee != null) {
				if (!StringUtils.isEmptyOrWhitespaceOnly(employee.getFirstName())) {
					dbEmployee.setFirstName(employee.getFirstName());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(employee.getLastName())) {
					dbEmployee.setLastName(employee.getLastName());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(employee.getDepartment())) {
					dbEmployee.setDepartment(employee.getDepartment());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(employee.getSkills())) {
					dbEmployee.setSkills(employee.getSkills());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(employee.getType())) {
					dbEmployee.setType(employee.getType());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(employee.getCompanyName())) {
					dbEmployee.setCompanyName(employee.getCompanyName());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(employee.getEmail())) {
					dbEmployee.setEmail(employee.getEmail());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(employee.getCompanyName())) {
					Company company=isRegisteredComapany(employee.getCompanyName());
					if(company!=null){
						dbEmployee.setCompany(company);
					}else{
						CompanyServiceImplementation companyService = new CompanyServiceImplementation();
						Company newCompany=new Company();
						newCompany.setCompanyName(employee.getCompanyName());
						dbEmployee.setCompany(companyService.add(newCompany));
					}
				    
				} 
				session.save(dbEmployee);
				session.flush();
				tx.commit();
			} else {
				logger.error("Can't update, employee not found with Id :-" + id);
				throw new EntityNotFoundException("Can't update, employee not found with Id :-" + id);
			}
			return dbEmployee;
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, patch() of employee");
			throw new ConstraintViolationException(
					"unable to update, record already present with email :-" + employee.getEmail(), null, null);
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
			Query query = session.createQuery("select project from ProjectEmployee where id = :id");
			query.setParameter("id", id);
			projectResult = query.list();
			if (projectResult.size() == 0) {
				logger.error("No Project assign to this employee " + id);
				throw new EntityNotFoundException("No Project assign to this employee " + id);
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
					Project project = (Project) session.get(Project.class, (long) projectId);
					if (project != null) {
						org.hibernate.Transaction txProject = session.beginTransaction();
						projectEmployee.setEmployee(employee);
						projectEmployee.setProject(project);
						session.save(projectEmployee);
						txProject.commit();
					} else {
						logger.error("project does not exist with id " + projectId);
						throw new EntityNotFoundException(
								"unable to  assign employee, project does not exist with id :-" + projectId);
					}
				}
				return projectEmployee;
			} else {
				logger.error("employee must be of employee type but It is :-" + employee.getType() + "type");
				throw new MediaTypeException("can't assign project, employee must be of employee type");
			}
		} catch (HibernateException exception) {
			logger.error("trying to insert duplicate value");
			throw new ConstraintViolationException(
					"unable to assign, emp already present with email :-" + employee.getEmail(), null, null);
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
	}
<<<<<<< HEAD
	
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
=======

	public Token userAuthentication(UserCredentials userCredentials) {
		String userName = userCredentials.getUserName();
		Session session = HibernateUtils.getSession();
		String tokenString = "";
		try {
			Query query = session.createQuery("from Employee  where email = :email");
			query.setParameter("email", userName);
			List<Employee> dbUser = query.list();
			if (!dbUser.isEmpty()) {

				Random random = new SecureRandom();
				tokenString = new BigInteger(130, random).toString(32);

				Token token = new Token();
				token.setToken(tokenString);
				token.setUserName(userName);
				token.setExpiryTime(Calendar.getInstance().getTime());
				session.save(token);
				session.beginTransaction().commit();
				return token;
			} else {
>>>>>>> 6ca684102c93a879cbab27e7bba963124af17a3d
				throw new EntityNotFoundException("userName or password invalid");
			}
		} catch (HibernateException exception) {
			throw new EntityNotFoundException("unable to generate token");
		} finally {
			session.close();
		}

	}

	public Company isRegisteredComapany(String companyName) {
		Session session = HibernateUtils.getSession();
		try {
			Query query = session.createQuery("from Company where company_name = :company_name");
			query.setParameter("company_name", companyName);
			Company company = (Company) query.uniqueResult();
			return company;
		} catch (HibernateException exception) {
			throw new EntityNotFoundException("unable to process your request");
		} finally {
			session.close();
		}
	}

}