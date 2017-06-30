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
import com.synerzip.projectmanagementapp.exception.FieldCanNotEmpty;
import com.synerzip.projectmanagementapp.exception.UserAlreadyPresent;
import com.synerzip.projectmanagementapp.model.Company;
import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.model.User;
import com.synerzip.projectmanagementapp.services.CompanyServices;

public class CompanyServiceImplementation implements CompanyServices {

	private static final Logger logger = Logger.getLogger(CompanyServiceImplementation.class);

	public Company get(long companyId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		Company company;
		try {
			company = (Company) session.get(Company.class, companyId);
			if (company == null) {
				logger.error("company not found  with companyId :-" + companyId);
				throw new EntityNotFoundException("record not found with id " + companyId);
			}
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, get() of company for companyId :-" + companyId);
			throw new HibernateException("unable to process your request");
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
		return company;
	}

	public PageResult gets(int start, int size, int companyId, String content) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		if (org.apache.commons.lang.StringUtils.isEmpty(content)) {
			try {
				int count = ((Long) session.createQuery("select count(*) from Company where company_id=" + companyId)
						.uniqueResult()).intValue();
				if (count > 0) {
					Query query = session.createQuery(
							"select new Company(c.companyId, c.companyName, c.companyAddress, c.companyContactNumber) from Company c where company_id="
									+ companyId);
					query.setFirstResult(start);
					query.setMaxResults(size);
					List<Company> companees = query.list();
					session.flush();
					PageResult pageResults = new PageResult();
					pageResults.setData(companees);
					pageResults.setTotalResult(count);
					return pageResults;
				} else {
					logger.error("Company not found, table is empty ");
					throw new EntityNotFoundException("no record found ");
				}
			} catch (HibernateException exception) {
				logger.error("abnormal ternination, gets() of Company");
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
		logger.info("session open successfully");
		entityManager.getTransaction().begin();
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		try {

			try {
				fullTextEntityManager.createIndexer().startAndWait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
					.forEntity(Company.class).get();
			org.apache.lucene.search.Query query = queryBuilder.keyword()
					.onFields("companyId", "companyName", "companyAddress", "companyContactNumber").matching(content)
					.createQuery();
			javax.persistence.Query fullTextQuery = fullTextEntityManager.createFullTextQuery(query, Company.class);
			int count = fullTextQuery.getResultList().size();
			fullTextQuery.setFirstResult(start);
			fullTextQuery.setMaxResults(size);
			List<Company> projectResult = fullTextQuery.getResultList();
			if (projectResult.size() != 0) {
				PageResult pageResults = new PageResult();
				pageResults.setData(projectResult);
				pageResults.setTotalResult(count);
				return pageResults;
			} else {
				logger.error("does not found any matched record with content:-" + content);
				throw new NotFoundException("No record matching with " + content);
			}
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, search() of company");
			throw new HibernateException("unable to process your request");
		} finally {
			if (fullTextEntityManager != null) {
				fullTextEntityManager.close();
				logger.info("session closed successfully");
			}
			fullTextEntityManager = null;
		}
	}

	public void add(User user) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		Company company = new Company();
		try {
			String companyName = user.getCompanyName();
			company.setCompanyName(companyName);
			session.save(company);
			tx.commit();
			UserServicesImplementation userService = new UserServicesImplementation();
			userService.add(user, company.getCompanyId());
		} catch (ConstraintViolationException exception) {
			logger.error("abnormal ternination, add() of company");
			throw new ConstraintViolationException("company already present with name-- " + company.getCompanyName(),
					null, null);
		}catch (UserAlreadyPresent exception) {
			logger.error("abnormal ternination, add() of company");
			throw new UserAlreadyPresent("user already present with name-- " + user.getEmail());
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
		
	}

	public String delete(long companyId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			String deleteQuery = "DELETE FROM Company WHERE company_id = :company_id";
			Query query = session.createQuery(deleteQuery);
			query.setParameter("company_id", companyId);
			int affectedRow = query.executeUpdate();
			if (affectedRow == 0) {
				logger.error("record already deleted or not exist");
				throw new EntityNotFoundException("no record found with id:-" + companyId);
			}
			tx.commit();
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, delete() of company");
			throw new HibernateException("unable to process your request");
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
		return "record deleted";
	}

	public Company update(Company company, long projectId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			if (StringUtils.isEmptyOrWhitespaceOnly(company.getCompanyName())) {
				logger.error("company name is empty");
				throw new FieldCanNotEmpty("company name must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(company.getCompanyAddress())) {
				logger.error("company address is empty");
				throw new FieldCanNotEmpty("company address must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(company.getCompanyContactNumber())) {
				logger.error("company contact number is empty");
				throw new FieldCanNotEmpty("company contact number must be filled");
			} else {
				session.update(company);
				tx.commit();
			}
			return company;
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, update() of company");
			throw new ConstraintViolationException("record already present with title-- " + company.getCompanyName(),
					null, null);
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
	}

	public Company patch(Company company, long companyId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			Company dbCompany = (Company) session.get(Company.class, companyId);
			if (dbCompany != null) {
				if (!StringUtils.isEmptyOrWhitespaceOnly(company.getCompanyName())) {
					dbCompany.setCompanyName((company.getCompanyName()));
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(company.getCompanyAddress())) {
					dbCompany.setCompanyAddress((company.getCompanyAddress()));
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(company.getCompanyContactNumber())) {
					dbCompany.setCompanyContactNumber((company.getCompanyContactNumber()));
				}
				session.save(dbCompany);
				session.flush();
				tx.commit();
			} else {
				logger.error("Can't update, company not found with companyId :-" + companyId);
				throw new EntityNotFoundException("Can't update, project not found with companyId :-" + companyId);
			}
			return dbCompany;
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, patch() of company");
			throw new ConstraintViolationException("record already present with title-- " + company.getCompanyName(),
					null, null);
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
	}
}