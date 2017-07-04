package com.synerzip.projectmanagementapp.serviceimplementation;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import com.mysql.jdbc.StringUtils;
import com.synerzip.projectmanagementapp.dbconnection.HibernateUtils;
import com.synerzip.projectmanagementapp.exception.CanNotChangePassword;
import com.synerzip.projectmanagementapp.exception.MediaTypeException;
import com.synerzip.projectmanagementapp.exception.UserAlreadyPresent;
import com.synerzip.projectmanagementapp.model.ChangePassword;
import com.synerzip.projectmanagementapp.model.Company;
import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.model.Project;
import com.synerzip.projectmanagementapp.model.ProjectEmployee;
import com.synerzip.projectmanagementapp.model.Token;
import com.synerzip.projectmanagementapp.model.User;
import com.synerzip.projectmanagementapp.model.UserCredentials;
import com.synerzip.projectmanagementapp.services.UserServices;

public class UserServicesImplementation implements UserServices {

	private static final Logger logger = Logger.getLogger(UserServicesImplementation.class);

	public User get(long id, long companyId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		User user;
		try {
			Query query = session.getNamedQuery("getUserById");
			query.setLong("id", id);
			query.setLong("companyid", companyId);
			user = (User) query.uniqueResult();
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

	public PageResult gets(int start, int size, String content, long companyId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		if (org.apache.commons.lang.StringUtils.isEmpty(content)) {
			try {
				int count = ((Long) session
						.createQuery(
								"select count(*) from com.synerzip.projectmanagementapp.model.User where company_id = :company_id and type = :usertype")
						.setParameter("company_id", companyId).setParameter("usertype", "employee").uniqueResult())
								.intValue();
				if (count > 0) {
					Query query = session.createQuery(
							"select new User(u.id, u.firstName, u.lastName, u.type, u.email, u.mobile) from User u  where company_id = :company_id and type = :usertype");
					query.setFirstResult(start);
					query.setMaxResults(size);
					query.setParameter("company_id", companyId);
					query.setParameter("usertype", "employee");
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
			return search(start, size, content, companyId);
		}
	}

	public PageResult search(int start, int size, String content, long companyId) {
		EntityManager entityManager = Persistence.createEntityManagerFactory("HibernatePersistence")
				.createEntityManager();
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		try {

			/*
			 * try { fullTextEntityManager.createIndexer().startAndWait(); }
			 * catch (InterruptedException e) { e.printStackTrace(); }
			 */

			QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(User.class).get();
			org.apache.lucene.search.Query query = qb.keyword().onFields("firstName", "lastName", "mobile", "email")
					.matching(content).createQuery();
			javax.persistence.Query fullTextQuery = fullTextEntityManager.createFullTextQuery(query, User.class);
			((FullTextQuery) fullTextQuery).enableFullTextFilter("UserFilterByCompanyId").setParameter("companyId",
					companyId);
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

	public User add(User user, long companyId){
			Session	session = HibernateUtils.getSession();
			logger.info("session open successfully");
			org.hibernate.Transaction tx = session.beginTransaction();
		try {
			Company company = (Company) session.get(Company.class, companyId);
			user.setCompany(company);
			session.save(user);
			tx.commit();
			return user;
		} catch (RuntimeException exception) {
			logger.error("abnormal ternination, add() of user");
			throw new UserAlreadyPresent("user name already exist");
		} finally {
				session.close();
				logger.info("session closed successfully");
		}
	}

	public String delete(long id, long companyId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			String deleteQuery = "DELETE FROM User WHERE id = :id and company_id = :company_id";
			Query query = session.createQuery(deleteQuery);
			query.setParameter("id", id);
			query.setParameter("company_id", companyId);
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
			User dbUser = (User) session.get(User.class, id);
			if (dbUser != null) {
				if (!StringUtils.isEmptyOrWhitespaceOnly(user.getFirstName())) {
					dbUser.setFirstName(user.getFirstName());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(user.getLastName())) {
					dbUser.setLastName(user.getLastName());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(user.getMobile())) {
					dbUser.setMobile(user.getMobile());
				}
				session.save(dbUser);
				session.flush();
				tx.commit();
			}
			return dbUser;
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
				if (!StringUtils.isEmptyOrWhitespaceOnly(user.getMobile())) {
					dbUser.setMobile(user.getMobile());
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
				List<Long> projectIds = user.getProjectIds();
				ProjectEmployee projectEmployee = new ProjectEmployee();
				for (Long projectId : projectIds) {
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
		String userName = userCredentials.getUserName();
		String userPassword = userCredentials.getUserPassword();
		Session session = HibernateUtils.getSession();
		try {
			Query query = session.createQuery(
					"select new User(u.id, u.firstName, u.lastName, u.type, u.email, u.mobile, u.company) from User u  where email = :email and password = :password");
			query.setParameter("email", userName);
			query.setParameter("password", userPassword);
			User user = (User) query.uniqueResult();
			if (user != null) {

				String tokenString = UUID.randomUUID().toString();

				Token token = new Token();
				token.setToken(tokenString);
				token.setUser(user);
				token.setExpiryTime(Calendar.getInstance().getTime());
				session.save(token);
				session.beginTransaction().commit();
				return token;
			} else {
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

	public List<User> getEmployees(int start, int size, String content, long companyId) {
		Session session = HibernateUtils.getSession();
		if (org.apache.commons.lang.StringUtils.isEmpty(content)) {
			try {
				Query getEmployee = session.createQuery(
						"select new User(u.id, u.firstName, u.lastName, u.email) from User u  where type = :query and company_id = :company_id");
				getEmployee.setParameter("query", "employee");
				getEmployee.setParameter("company_id", companyId);
				getEmployee.setFirstResult(start);
				getEmployee.setMaxResults(size);
				List<User> employees = (List<User>) getEmployee.list();
				if (employees.isEmpty()) {
					throw new EntityNotFoundException("unable to process your request");
				}
				return employees;
			} catch (HibernateException exception) {
				throw new EntityNotFoundException("unable to process your request");
			} finally {
				session.close();
			}
		} else {
			EntityManager entityManager = Persistence.createEntityManagerFactory("HibernatePersistence")
					.createEntityManager();
			FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
			try {

				/*
				 * try { fullTextEntityManager.createIndexer().startAndWait(); }
				 * catch (InterruptedException e) { e.printStackTrace(); }
				 */

				QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(User.class)
						.get();
				org.apache.lucene.search.Query query = qb.keyword().onFields("firstName", "lastName", "email")
						.matching(content).createQuery();
				javax.persistence.Query fullTextQuery = fullTextEntityManager.createFullTextQuery(query, User.class);
				((FullTextQuery) fullTextQuery).enableFullTextFilter("UserFilterByCompanyId").setParameter("companyId",
						companyId);
				((FullTextQuery) fullTextQuery).enableFullTextFilter("UserFilterByType").setParameter("type",
						"employee");
				fullTextQuery.setFirstResult(start);
				fullTextQuery.setMaxResults(size);
				int count = fullTextQuery.getResultList().size();
				List<User> userResult = fullTextQuery.getResultList();
				return userResult;
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

	}

	public String assignProject(long userId, long projectId) {
		Session session = HibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			Project project = (Project) session.get(Project.class, projectId);
			User user = (User) session.get(User.class, userId);
			ProjectEmployee assign = new ProjectEmployee();
			assign.setProject(project);
			assign.setUser(user);
			session.save(assign);
			tx.commit();
		} catch (HibernateException exception) {
			throw new EntityNotFoundException("project already assigned to this employee");
		} finally {
			session.close();
		}
		return "project assigned to employee";
	}

	public User profile(long userId) {
		Session session = HibernateUtils.getSession();
		try {
			Query query = session
					.createQuery(
							"select new User(u.firstName, u.lastName, u.type, u.email, u.mobile, u.company) from User u where id = :id")
					.setParameter("id", userId);
			User user = (User) query.uniqueResult();
			return user;

		} catch (HibernateException exception) {
			throw new EntityNotFoundException("user record not found");
		} finally {
			session.close();
		}
	}

	public String changePassword(String username, ChangePassword data) {
		String userName = data.getUserName();
		String oldPass = data.getOldPassword();
		String newPass = data.getNewPassword();
		Session session = HibernateUtils.getSession();
		ChangePassword changePass = null;
		try {
			Query query = session.createQuery("select password from User where email = :user_name");
			query.setParameter("user_name", userName);
			String dbPass = (String) query.uniqueResult();
			if (dbPass.equals(oldPass)) {
				Query updateQuery = session
						.createQuery("UPDATE User SET password = :new_pass where email = :user_name");
				updateQuery.setParameter("new_pass", newPass);
				updateQuery.setParameter("user_name", username);
				int affectedRow = updateQuery.executeUpdate();
				if (affectedRow == 0) {
					logger.error("incorrect password");
					throw new CanNotChangePassword("incorrect password : password can not change");
				}
				session.beginTransaction().commit();
				changePass = new ChangePassword();
				changePass.setNewPassword(newPass);
				changePass.setUserName(userName);
			}
		} catch (HibernateException exception) {
			logger.error("incorrect password");
			throw new HibernateException("unable to process your request");
		} finally {
			session.close();
			logger.error("session close");
		}
		return "password changed";
	}
}