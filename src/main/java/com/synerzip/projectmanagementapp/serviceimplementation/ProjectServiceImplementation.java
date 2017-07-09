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
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import com.mysql.jdbc.StringUtils;
import com.synerzip.projectmanagementapp.dbconnection.HibernateUtils;
import com.synerzip.projectmanagementapp.exception.FieldCanNotEmpty;
import com.synerzip.projectmanagementapp.exception.MediaTypeException;
import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.model.Project;
import com.synerzip.projectmanagementapp.model.ProjectEmployee;
import com.synerzip.projectmanagementapp.model.User;
import com.synerzip.projectmanagementapp.services.ProjectServices;

public class ProjectServiceImplementation implements ProjectServices {

	private static final Logger logger = Logger.getLogger(ProjectServiceImplementation.class);

	public Project get(long projectId, long companyId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		Project project;
		try {
			Query query = session.getNamedQuery("getProjectById");
			query.setLong("projectid", projectId);
			query.setLong("companyid", companyId);
			project = (Project) query.uniqueResult();
			if (project == null) {
				logger.error("project not found  with projectId :-" + projectId);
				throw new EntityNotFoundException("record not found with id " + projectId);
			}
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, get() of project for projectId :-" + projectId);
			throw new HibernateException("unable to process your request");
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
		return project;
	}

	public PageResult gets(int start, int size, String content, long companyId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		if (org.apache.commons.lang.StringUtils.isEmpty(content)) {
			try {
				int count = ((Long) session.createQuery("select count(*) from Project where company_id = :company_id")
						.setParameter("company_id", companyId).uniqueResult()).intValue();
				if (count > 0) {
					Query query = session.createQuery(
							"select new Project(p.projectId, p.projectTitle, p.technologyUsed, p.projectDescription, p.projectFeature) from Project p where company_id = :company_id");
					query.setFirstResult(start);
					query.setMaxResults(size);
					query.setParameter("company_id", companyId);
					List<Project> projects = query.list();
					session.flush();
					PageResult pageResults = new PageResult();
					pageResults.setData(projects);
					pageResults.setTotalResult(count);
					return pageResults;
				} else {
					logger.error("project not found, table is empty ");
					throw new EntityNotFoundException("no record found ");
				}
			} catch (HibernateException exception) {
				logger.error("abnormal ternination, gets() of project");
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
		logger.info("session open successfully");
		entityManager.getTransaction().begin();
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		try {

			/*
			 * try { fullTextEntityManager.createIndexer().startAndWait(); }
			 * catch (InterruptedException e) { e.printStackTrace(); }
			 */

			QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
					.forEntity(Project.class).get();
			org.apache.lucene.search.Query query = queryBuilder.keyword()
					.onFields("projectTitle", "technologyUsed", "projectFeature", "projectDescription")
					.matching(content +"*").createQuery();
			javax.persistence.Query fullTextQuery = fullTextEntityManager.createFullTextQuery(query, Project.class);
			fullTextQuery.setFirstResult(start);
			fullTextQuery.setMaxResults(size);
			((FullTextQuery) fullTextQuery).enableFullTextFilter("ProjectFilterByCompanyId").setParameter("companyId",
					companyId);
			int count = fullTextQuery.getResultList().size();
			List<Project> projectResult = fullTextQuery.getResultList();
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
			logger.error("abnormal ternination, search() of project");
			throw new HibernateException("unable to process your request");
		} finally {
			if (fullTextEntityManager != null) {
				fullTextEntityManager.close();
				logger.info("session closed successfully");
			}
			fullTextEntityManager = null;
		}
	}

	public Project add(Project project) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			if (StringUtils.isEmptyOrWhitespaceOnly(project.getProjectTitle())) {
				logger.error("project Title is empty");
				throw new FieldCanNotEmpty("project Title must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(project.getProjectFeature())) {
				logger.error("project Feature is empty");
				throw new FieldCanNotEmpty("project Feature must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(project.getProjectDescription())) {
				logger.error("project Description is empty");
				throw new FieldCanNotEmpty("project Description must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(project.getTechnologyUsed())) {
				logger.error("project TechnologyUsed is empty");
				throw new FieldCanNotEmpty("project TechnologyUsed  must be filled");
			} else {
				session.save(project);
				tx.commit();
			}
			return project;
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, add() of project");
			throw new ConstraintViolationException("record already present with title-- " + project.getProjectTitle(),
					null, null);
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
	}

	public String delete(long projectId, long companyId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			Query relQuery = session.createQuery("DELETE FROM ProjectEmployee WHERE project_id = :project_id");
			relQuery.setParameter("project_id", projectId);
			relQuery.executeUpdate();
			String deleteQuery = "DELETE FROM Project WHERE project_id = :project_id and company_id = :company_id";
			Query query = session.createQuery(deleteQuery);
			query.setParameter("project_id", projectId);
			query.setParameter("company_id", companyId);
			int affectedRow = query.executeUpdate();
			if (affectedRow == 0) {
				logger.error("record already deleted or not exist");
				throw new EntityNotFoundException("no record found with id:-" + projectId);
			}
			tx.commit();
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, delete() of project");
			throw new HibernateException("unable to process your request");
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
		return "record deleted";
	}
	
	public String unAssign(long projectId, long userId, long companyId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			Query relQuery = session.createQuery("DELETE FROM ProjectEmployee WHERE project_id = :project_id and emp_id = :emp_id");
			relQuery.setParameter("project_id", projectId);
			relQuery.setParameter("emp_id", userId);
			relQuery.executeUpdate();
			tx.commit();
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, unAssign() of project");
			throw new HibernateException("unable to process your request");
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
		return "unassigned project";
	}
	
	public Project update(Project project, long projectId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			if (StringUtils.isEmptyOrWhitespaceOnly(project.getProjectTitle())) {
				logger.error("project Title is empty");
				throw new FieldCanNotEmpty("project Title must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(project.getProjectFeature())) {
				logger.error("project Feature is empty");
				throw new FieldCanNotEmpty("project Feature must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(project.getProjectDescription())) {
				logger.error("project Description is empty");
				throw new FieldCanNotEmpty("project Description must be filled");
			} else if (StringUtils.isEmptyOrWhitespaceOnly(project.getTechnologyUsed())) {
				logger.error("project TechnologyUsed is empty");
				throw new FieldCanNotEmpty("project TechnologyUsed  must be filled");
			} else {
				session.saveOrUpdate(project);
				tx.commit();
			}
			return project;
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, update() of project");
			throw new ConstraintViolationException("record already present with title-- " + project.getProjectTitle(),
					null, null);
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
	}

	public Project patch(Project project, long projectId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			Project dbProject = (Project) session.get(Project.class, projectId);
			if (dbProject != null) {
				if (!StringUtils.isEmptyOrWhitespaceOnly(project.getProjectTitle())) {
					dbProject.setProjectTitle(project.getProjectTitle());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(project.getProjectFeature())) {
					dbProject.setProjectFeature(project.getProjectFeature());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(project.getProjectDescription())) {
					dbProject.setProjectDescription(project.getProjectDescription());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(project.getTechnologyUsed())) {
					dbProject.setTechnologyUsed(project.getTechnologyUsed());
				}
				session.save(dbProject);
				session.flush();
				tx.commit();
			} else {
				logger.error("Can't update, project not found with projectId :-" + projectId);
				throw new EntityNotFoundException("Can't update, project not found with projectId :-" + projectId);
			}
			return dbProject;
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, patch() of project");
			throw new ConstraintViolationException("record already present with title-- " + project.getProjectTitle(),
					null, null);
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
	}

	public List<User> assigned(long projectId) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		List<User> empResult = null;
		try {
			Query query = session.createQuery("select user from ProjectEmployee where project_id = :project_id");
			query.setParameter("project_id", projectId);
			empResult = query.list();
			if (empResult.size() == 0) {
				logger.error("No User assign to this project " + projectId);
				throw new EntityNotFoundException("No User assign to this project " + projectId);
			}
		} catch (HibernateException exception) {
			logger.error("abnormal ternination, assigned() of project");
			throw new HibernateException("unable to process your request");
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
		return empResult;
	}

	public ProjectEmployee assign(Project project) {
		Session session = HibernateUtils.getSession();
		logger.info("session open successfully");
		org.hibernate.Transaction txProject = session.beginTransaction();
		try {
			session.save(project);
			txProject.commit();
			List<Long> empIds = project.getEmpIds();
			if (empIds.size() != 0) {
				ProjectEmployee projectEmployee = new ProjectEmployee();
				for (Long empId : empIds) {
					User user = (User) session.get(User.class, (long) empId);
					if (user != null) {
						String empType = user.getType();
						if (empType.equals("employee")) {
							org.hibernate.Transaction txEmployee = session.beginTransaction();
							projectEmployee.setUser(user);
							projectEmployee.setProject(project);
							session.save(projectEmployee);
							txEmployee.commit();
						} else {
							logger.error("empId must be of User Type but it is :-" + user.getType());
							throw new MediaTypeException("User must be of employee Type");
						}

					} else {
						logger.error("User not exist with empId :-" + empId);
						throw new EntityNotFoundException("can't assign, emp not exist");
					}
				}
				return projectEmployee;
			} else {
				logger.error("can't assign, empIds are empty");
				throw new HibernateException("can't assign, provide empIds");
			}
		} catch (HibernateException exception) {
			logger.error("trying to insert duplicate value");
			throw new ConstraintViolationException(
					"unable to assign, project already present with title :-" + project.getProjectTitle(), null, null);
		} finally {
			session.close();
			logger.info("session closed successfully");
		}
	}

	public List<Project> getProjects(int start, int size, String content, long companyId) {
		if (org.apache.commons.lang.StringUtils.isEmpty(content)) {
			Session session = HibernateUtils.getSession();
			try {
				Query getProject = session.createQuery(
						"select new Project(p.projectId, p.projectTitle) from Project p where company_id = :company_id");
				getProject.setParameter("company_id", companyId);
				getProject.setFirstResult(start);
				getProject.setMaxResults(size);
				List<Project> projects = (List<Project>) getProject.list();
				if (projects.isEmpty()) {
					throw new EntityNotFoundException("unable to process your request");
				}
				return projects;
			} catch (HibernateException exception) {
				throw new EntityNotFoundException("unable to process your request");
			} finally {
				session.close();
			}
		} else {
			EntityManager entityManager = Persistence.createEntityManagerFactory("HibernatePersistence")
					.createEntityManager();
			logger.info("session open successfully");
			entityManager.getTransaction().begin();
			FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
			try {

				/*
				 * try { fullTextEntityManager.createIndexer().startAndWait(); }
				 * catch (InterruptedException e) { e.printStackTrace(); }
				 */

				QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
						.forEntity(Project.class).get();
				org.apache.lucene.search.Query query = queryBuilder.keyword()
						.onFields("technologyUsed", "projectFeature", "projectDescription", "projectTitle")
						.matching(content+"*").createQuery();
				javax.persistence.Query fullTextQuery = fullTextEntityManager.createFullTextQuery(query, Project.class);
				fullTextQuery.setFirstResult(start);
				fullTextQuery.setMaxResults(size);
				((FullTextQuery) fullTextQuery).enableFullTextFilter("ProjectFilterByCompanyId")
						.setParameter("companyId", companyId);
				List<Project> projectResult = fullTextQuery.getResultList();
				return projectResult;
			} catch (HibernateException exception) {
				logger.error("abnormal ternination, search() of project");
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

}