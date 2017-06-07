package com.synerzip.projectmanagementapp.serviceimplementation;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.ws.rs.core.SecurityContext;
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
import com.synerzip.projectmanagementapp.exception.FieldCanNotEmpty;
import com.synerzip.projectmanagementapp.exception.MediaTypeException;
import com.synerzip.projectmanagementapp.model.Company;
import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.model.Project;
import com.synerzip.projectmanagementapp.model.ProjectEmployee;
import com.synerzip.projectmanagementapp.model.Token;
import com.synerzip.projectmanagementapp.model.User;
import com.synerzip.projectmanagementapp.model.UserCredentials;
import com.synerzip.projectmanagementapp.services.UserServices;

public class UserServicesImplementation implements UserServices {

	static final Logger logger = Logger.getLogger(UserServicesImplementation.class);
	
	public List<User> get(long id/*, SecurityContext securityContext*/) {
		/*System.out.println(securityContext.getUserPrincipal().getName());*/
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		List<User> user;
		try {
			Query query = session.getNamedQuery("getById");
			query.setLong("id", id);
			user = query.list();
			if (user == null) {
				logger.error("user not found  with empid :-" + id);
				throw new EntityNotFoundException("record not found with id " + id);
			}
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, get() of user for id :-" + id);
			throw new HibernateException("unable to process your request");
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
		return user;
	}

	public PageResult gets(int start, int size, String content) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		if (org.apache.commons.lang.StringUtils.isEmpty(content)) {
			try {
				int count = ((Long) session
						.createQuery("select count(*) from com.synerzip.projectmanagementapp.model.User")
						.uniqueResult()).intValue();
				if (count > 0) {
					Query query = session.createQuery("from com.synerzip.projectmanagementapp.model.User");
					query.setFirstResult(start);
					query.setMaxResults(size);
					List<User> users = query.list();
					session.flush();
					PageResult pageResults = new PageResult();
					pageResults.setData(users);
					pageResults.setTotalResult(count);
					return pageResults;
				} else {
					logger.error("User not found ");
					throw new EntityNotFoundException("no record found ");
				}
			} catch (Exception exception) {
				logger.error("abnormal ternination, gets() of User");
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
			 
			QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(User.class)
					.get();
			org.apache.lucene.search.Query query = qb.keyword()
					.onFields("firstName", "lastName", "department", "skills", "email").matching(content)
					.createQuery();
			javax.persistence.Query fullTextQuery = fullTextEntityManager.createFullTextQuery(query, User.class);
			fullTextQuery.setFirstResult(start);
			fullTextQuery.setMaxResults(size);
			int count = fullTextQuery.getResultList().size();
			List<User> userResult = fullTextQuery.getResultList();
			if (userResult.size() != 0) {
				PageResult pageResults = new PageResult();
				pageResults.setData(userResult);
				pageResults.setTotalResult(count);
				return pageResults;
			} else {
				logger.error("does not found any matched record with content:-" + content);
				throw new EntityNotFoundException("No record matching with " + content);
			}

		} catch (HibernateException exception) {
			logger.error("abnormal ternination, search() of user");
			throw new HibernateException("unable to process your request");
		} finally {
			if (fullTextEntityManager != null) {
				fullTextEntityManager.close();
				logger.info("session closed successfully");
			}
			fullTextEntityManager = null;
		}
	}

	public User add(User user) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		String companyName = user.getCompanyName();
		try {
			/*Company company = isRegisteredComapany(companyName);
			if (company != null) {
				throw new ConstraintViolationException("company already present with company name - " + user.getCompanyName(), null,
						null);
			} else {
				company.setCompanyName(companyName);
				CompanyServiceImplementation companyService = new CompanyServiceImplementation();
				company = companyService.add(company);
			}*/

			if (StringUtils.isEmptyOrWhitespaceOnly(user.getFirstName())) {
				logger.error("first name is empty");
				throw new FieldCanNotEmpty("first name must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(user.getLastName())) {
				logger.error("last name is empty");
				throw new FieldCanNotEmpty("last name must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(user.getDepartment())) {
				logger.error("department is empty");
				throw new FieldCanNotEmpty("department must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(user.getSkills())) {
				logger.error("skills is empty");
				throw new FieldCanNotEmpty("skills must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(user.getType())) {
				logger.error("user type is empty");
				throw new FieldCanNotEmpty("user type must be filled");
			} else {
				user.setCompany(null);
				session.save(user);
				tx.commit();
			}
			return user;
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, add() of user");
			throw new ConstraintViolationException("record already present with email - " + user.getEmail(), null,
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
			String deleteQuery = "DELETE FROM User WHERE id = :id";
			Query query = session.createQuery(deleteQuery);
			query.setParameter("id", id);
			int affectedRow = query.executeUpdate();
			if (affectedRow == 0) {
				logger.error("record already deleted or not exist");
				throw new EntityNotFoundException("record already deleted");
			}
			tx.commit();
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, delete() of user for Id :-" + id);
			throw new HibernateException("unable to process your request");
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
		return "record deleted";
	}

	public User update(User user, long id) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			/*
			 * if (StringUtils.isEmptyOrWhitespaceOnly(user.getFirstName()))
			 * { logger.error("first name is empty"); throw new
			 * FieldCanNotEmpty("first  name must be filled"); } else if
			 * (StringUtils.isEmptyOrWhitespaceOnly(user .getDepartment()))
			 * { logger.error("department is empty"); throw new
			 * FieldCanNotEmpty("department must be filled"); } else if
			 * (StringUtils.isEmptyOrWhitespaceOnly(user .getSkills())) {
			 * logger.error("skills is empty"); throw new FieldCanNotEmpty(
			 * "skills must be filled"); } else { session.saveOrUpdate(user);
			 * tx.commit(); }
			 */
			if (!StringUtils.isEmptyOrWhitespaceOnly(user.getCompanyName())) {
				Company company=isRegisteredComapany(user.getCompanyName());
				if(company!=null){
					user.setCompany(company);
				}else{
					CompanyServiceImplementation companyService = new CompanyServiceImplementation();
					Company newCompany=new Company();
					newCompany.setCompanyName(user.getCompanyName());
					user.setCompany(companyService.add(newCompany));
				}
			    
			} else{
				logger.error("company name is empty"); 
				throw new FieldCanNotEmpty("company  name must be filled"); 
			}
			session.saveOrUpdate(user);
			tx.commit();
			return user;
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, update() of user");
			tx.rollback();
			throw new ConstraintViolationException("record already present with email - " + user.getEmail(), null,
					null);
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
	}

	public User patch(User user, long id) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			User dbUser = (User) session.get(User.class, id);
			if (dbUser != null) {
				if (!StringUtils.isEmptyOrWhitespaceOnly(user.getFirstName())) {
					dbUser.setFirstName(user.getFirstName());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(user.getLastName())) {
					dbUser.setLastName(user.getLastName());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(user.getDepartment())) {
					dbUser.setDepartment(user.getDepartment());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(user.getSkills())) {
					dbUser.setSkills(user.getSkills());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(user.getType())) {
					dbUser.setType(user.getType());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(user.getCompanyName())) {
					dbUser.setCompanyName(user.getCompanyName());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(user.getEmail())) {
					dbUser.setEmail(user.getEmail());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(user.getCompanyName())) {
					Company company=isRegisteredComapany(user.getCompanyName());
					if(company!=null){
						dbUser.setCompany(company);
					}else{
						CompanyServiceImplementation companyService = new CompanyServiceImplementation();
						Company newCompany=new Company();
						newCompany.setCompanyName(user.getCompanyName());
						dbUser.setCompany(companyService.add(newCompany));
					}
				    
				} 
				session.save(dbUser);
				session.flush();
				tx.commit();
			} else {
				logger.error("Can't update, user not found with Id :-" + id);
				throw new EntityNotFoundException("Can't update, user not found with Id :-" + id);
			}
			return dbUser;
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, patch() of user");
			throw new ConstraintViolationException(
					"unable to update, record already present with email :-" + user.getEmail(), null, null);
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
			Query query = session.createQuery("select project from ProjectEmployee where emp_id = :emp_id");
			query.setParameter("emp_id", id);
			projectResult = query.list();
			if (projectResult.size() == 0) {
				logger.error("No Project assign to this user " + id);
				throw new EntityNotFoundException("No Project assign to this user " + id);
			}
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, assigned() of user");
			throw new HibernateException("unable to process your request");
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
		return projectResult;
	}

	public ProjectEmployee assign(User user) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction txUser = session.beginTransaction();
		try {
			session.save(user);
			txUser.commit();
			if (user.getType().equals("employee")) {
				List<Integer> projectIds = user.getProjectIds();
				ProjectEmployee projectEmployee = new ProjectEmployee();
				for (Integer projectId : projectIds) {
					Project project = (Project) session.get(Project.class, (long) projectId);
					if (project != null) {
						org.hibernate.Transaction txProject = session.beginTransaction();
						projectEmployee.setUser(user);
						projectEmployee.setProject(project);
						session.save(projectEmployee);
						txProject.commit();
					} else {
						logger.error("project does not exist with id " + projectId);
						throw new EntityNotFoundException(
								"unable to  assign user, project does not exist with id :-" + projectId);
					}
				}
				return projectEmployee;
			} else {
				logger.error("user must be of employee type but It is :-" + user.getType() + "type");
				throw new MediaTypeException("can't assign project, user must be of employee type");
			}
		} catch (HibernateException exception) {
			logger.error("trying to insert duplicate value");
			throw new ConstraintViolationException(
					"unable to assign, emp already present with email :-" + user.getEmail(), null, null);
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
	}

	
	public Token userAuthentication(UserCredentials userCredentials) {
		String userName=userCredentials.getUserName();
		String userPassword=userCredentials.getUserPassword();
		Session session = HibernateUtils.getSession();                             
		try{
			Query query=session.createQuery("from User  where email = :email and password = :password");
			query.setParameter("email", userName);
			query.setParameter("password", userPassword);
			User user=(User)query.uniqueResult();
			if(user!=null){
				
					String tokenString = UUID.randomUUID().toString();
					
					Token token=new Token();
					token.setToken(tokenString);
					token.setUser(user);
					token.setExpiryTime(Calendar.getInstance().getTime());
					session.save(token);
					session.beginTransaction().commit();
					return token;
			}else{
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


	public String token(long id) {
		Session session = HibernateUtils.getSession();
		try {
			Query query = session.createQuery("DELETE from Token where user_id = :user_id");
			query.setParameter("user_id", id);
			query.executeUpdate();
			session.beginTransaction().commit();
			return "token deleted";
		} catch (HibernateException exception) {
			throw new EntityNotFoundException("unable to process your request");
		} finally {
			session.close();
		}
	}

}