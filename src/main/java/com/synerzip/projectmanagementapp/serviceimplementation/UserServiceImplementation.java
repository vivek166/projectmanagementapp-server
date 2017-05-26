package com.synerzip.projectmanagementapp.serviceimplementation;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.ws.rs.NotFoundException;
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
import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.model.User;
import com.synerzip.projectmanagementapp.model.UserCredentials;
import com.synerzip.projectmanagementapp.services.UserServices;

public class UserServiceImplementation implements UserServices {

	static final Logger logger = Logger
			.getLogger(UserServiceImplementation.class);

	public User get(String userId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		User user;
		try {
			user = (User) session.get(User.class, userId);
			if (user == null) {
				logger.error("user not found  with userId :-" + userId);
				throw new EntityNotFoundException("record not found with id "
						+ userId);
			}
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, get() of user for userId :-"
					+ userId);
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
						.createQuery(
								"select count(*) from com.synerzip.projectmanagementapp.model.User")
						.uniqueResult()).intValue();
				if (count > 0) {
					Query query = session
							.createQuery("from com.synerzip.projectmanagementapp.model.User");
					query.setFirstResult(start);
					query.setMaxResults(size);
					List<User> users = query.list();
					session.flush();
					PageResult pageResults = new PageResult();
					pageResults.setData(users);
					pageResults.setTotalResult(count);
					return pageResults;
				} else {
					logger.error("User not found, table is empty ");
					throw new EntityNotFoundException("no record found ");
				}
			} catch (HibernateException exception) {
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
		EntityManager entityManager = Persistence.createEntityManagerFactory(
				"HibernatePersistence").createEntityManager();
		logger.info("session open successfully");
		entityManager.getTransaction().begin();
		FullTextEntityManager fullTextEntityManager = Search
				.getFullTextEntityManager(entityManager);
		try {

			try {
				fullTextEntityManager.createIndexer().startAndWait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			QueryBuilder queryBuilder = fullTextEntityManager
					.getSearchFactory().buildQueryBuilder()
					.forEntity(User.class).get();
			org.apache.lucene.search.Query query = queryBuilder
					.keyword()
					.onFields("userId", "userName", "companyName").matching(content)
					.createQuery();
			javax.persistence.Query fullTextQuery = fullTextEntityManager
					.createFullTextQuery(query, User.class);
			int count = fullTextQuery.getResultList().size();
			fullTextQuery.setFirstResult(start);
			fullTextQuery.setMaxResults(size);
			List<User> userResult = fullTextQuery.getResultList();
			if (userResult.size() != 0) {
				PageResult pageResults = new PageResult();
				pageResults.setData(userResult);
				pageResults.setTotalResult(count);
				return pageResults;
			} else {
				logger.error("does not found any matched record with content:-"
						+ content);
				throw new NotFoundException("No record matching with "
						+ content);
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
		try {
			if (StringUtils.isEmptyOrWhitespaceOnly(user.getUserId())) {
				logger.error("user Id is empty");
				throw new CanNotEmptyField("user Id must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(user.getCompanyName())) {
				logger.error("company name is empty");
				throw new CanNotEmptyField("company  name  must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(user.getUserName())) {
				logger.error("user name is empty");
				throw new CanNotEmptyField("user name must be filled");
			}  else {
				session.save(user);
				tx.commit();
			}
			return user;
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, add() of user");
			throw new ConstraintViolationException(
					"record already present with title-- "
							+ user.getUserId(), null, null);
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
	}

	public String delete(String userId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			String deleteQuery = "DELETE FROM User WHERE user_id = :user_id";
			Query query = session.createQuery(deleteQuery);
			query.setParameter("user_id", userId);
			int affectedRow = query.executeUpdate();
			if (affectedRow == 0) {
				logger.error("record already deleted or not exist");
				throw new EntityNotFoundException("no record found with id:-"
						+ userId);
			}
			tx.commit();
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, delete() of user");
			throw new HibernateException("unable to process your request");
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
		return "record deleted";
	}

	public User update(User user, String userId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			if (StringUtils.isEmptyOrWhitespaceOnly(user.getUserId())) {
				logger.error("user Id is empty");
				throw new CanNotEmptyField("user Id must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(user.getCompanyName())) {
				logger.error("company name is empty");
				throw new CanNotEmptyField("company  name  must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(user.getUserName())) {
				logger.error("user name is empty");
				throw new CanNotEmptyField("user name must be filled");
			} else {
				session.saveOrUpdate(user);
				tx.commit();
			}
			return user;
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, update() of user");
			throw new ConstraintViolationException(
					"record already present with title-- "
							+ user.getUserId(), null, null);
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
	}

	public User patch(User user, String userId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			User dbUser = (User) session.get(User.class, userId);
			if (dbUser != null) {
				if (!StringUtils.isEmptyOrWhitespaceOnly(user.getUserId())) {
					logger.error("user Id is empty");
					throw new CanNotEmptyField("user Id must be filled");
				}  if (!StringUtils.isEmptyOrWhitespaceOnly(user.getCompanyName())) {
					logger.error("company name is empty");
					throw new CanNotEmptyField("company  name  must be filled");
				}  if (!StringUtils.isEmptyOrWhitespaceOnly(user.getUserName())) {
					logger.error("user name is empty");
					throw new CanNotEmptyField("user name must be filled");
				} 
				session.save(dbUser);
				session.flush();
				tx.commit();
			} else {
				logger.error("Can't update, user not found with userId :-"
						+ userId);
				throw new EntityNotFoundException(
						"Can't update, user not found with userId :-"
								+ userId);
			}
			return dbUser;
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, patch() of user");
			throw new ConstraintViolationException(
					"record already present with title-- "
							+ user.getUserId(), null, null);
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
	}

	
	public UserCredentials userAuthentication(UserCredentials userCredentials) {
		String userId=userCredentials.getUserId();
		String userPassword=userCredentials.getUserPassword();
		Session session = HibernateUtils.getSession();
		UserCredentials token=new UserCredentials();
		User dbUser;
		try{
			dbUser=(User)session.get(User.class, userId);
			if(dbUser!=null){
				if(userId.equals(dbUser.getUserId()) && userPassword.equals(dbUser.getUserPassword())){
					token.setUserId(dbUser.getUserId());
					token.setUserPassword(dbUser.getUserPassword());
					return token;
				}
			}
		}catch(HibernateException exception){
			exception.printStackTrace();
		}finally {
			session.close();
		}
		return token;
	}
}