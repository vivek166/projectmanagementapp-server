package com.synerzip.projectmanagementapp.serviceimplementation;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.ws.rs.NotFoundException;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import com.mysql.jdbc.StringUtils;
import com.synerzip.projectmanagementapp.dbconnection.HibernateUtils;
import com.synerzip.projectmanagementapp.model.Employee;
import com.synerzip.projectmanagementapp.model.PageResult;
import com.synerzip.projectmanagementapp.model.Project;
import com.synerzip.projectmanagementapp.model.ProjectEmployee;
import com.synerzip.projectmanagementapp.services.ProjectServices;

public class ProjectServiceImplementation implements ProjectServices {
	static final Logger logger = Logger.getLogger(ProjectServiceImplementation.class);

	@SuppressWarnings("unused")
	public Project get(long projectId) {
		Session session = HibernateUtils.getSession();
		Project project;
		try {
			project = (Project) session.get(Project.class, projectId);
			project.setEmployees(null);
			if (project == null) {
				throw new EntityNotFoundException("record not found with id " + projectId);
			}
		} catch (HibernateException e) {
			throw new HibernateException("record not found with id " + projectId);
		} finally {
			session.close();
		}
		return project;
	}

	public PageResult gets(int start, int size, String content) {
		Session session = HibernateUtils.getSession();
		if (org.apache.commons.lang.StringUtils.isEmpty(content)) {
			try {
				int count = ((Long) session
						.createQuery("select count(*) from com.synerzip.projectmanagementapp.model.Project")
						.uniqueResult()).intValue();
				if (count > 0) {
					Query query = session.createQuery("from com.synerzip.projectmanagementapp.model.Project");
					query.setFirstResult(start);
					query.setMaxResults(size);
					List<Project> projects = query.list();
					session.flush();
					PageResult pageResults = new PageResult();
					pageResults.setData(projects);
					pageResults.setTotalResult(count);
					return pageResults;
				} else {
					throw new EntityNotFoundException("no record found ");
				}
			} catch (Exception e) {
				throw new EntityNotFoundException("no record found ");
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
		entityManager.getTransaction().begin();
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		try {
			// fullTextEntityManager.createIndexer().startAndWait();
			QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
					.forEntity(Project.class).get();
			org.apache.lucene.search.Query query = queryBuilder.keyword()
					.onFields("projectId", "technologyUsed", "projectFeature", "projectDescription").matching(content)
					.createQuery();
			javax.persistence.Query fullTextQuery = fullTextEntityManager.createFullTextQuery(query, Project.class);
			int count = fullTextQuery.getResultList().size();
			fullTextQuery.setFirstResult(start);
			fullTextQuery.setMaxResults(size);
			List<Project> projectResult = fullTextQuery.getResultList();
			if (projectResult.size() != 0) {
				PageResult pageResults = new PageResult();
				pageResults.setData(projectResult);
				pageResults.setTotalResult(count);
				return pageResults;
			} else {
				throw new NotFoundException("No record matching with " + content);
			}
		} catch (Exception e) {
			throw new NotFoundException("No record matching with " + content);
		} finally {
			if (fullTextEntityManager != null) {
				fullTextEntityManager.close();
			}
			fullTextEntityManager = null;
		}
	}

	public Project add(Project project) {
		Session session = HibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			session.save(project);
			// addProjectEmployee(project);
			tx.commit();
			return project;
		} catch (Exception e) {
			throw new ConstraintViolationException("record already present with title-- " + project.getProjectTitle(),
					null, null);
		} finally {
			session.close();
		}
	}

	public String delete(long projectId) {
		Session session = HibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			String deleteQuery = "DELETE FROM Project WHERE project_id = :project_id";
			Query query = session.createQuery(deleteQuery);
			query.setParameter("project_id", projectId);
			int affectedRow = query.executeUpdate();
			tx.commit();
			if (affectedRow == 0) {
				throw new ObjectNotFoundException("no record found", deleteQuery);
			}
		} catch (Exception e) {
			throw new ObjectNotFoundException(e, "database error");
		} finally {
			session.close();
		}
		return "record deleted";
	}

	public Project update(Project project, long projectId) {
		Session session = HibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			session.saveOrUpdate(project);
			tx.commit();
			return project;
		} catch (Exception e) {
			throw new HibernateException("record already with same project title " + project.getProjectTitle());
		} finally {
			session.close();
		}
	}

	public Project patch(Project project, long projectId) {
		Session session = HibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			Project dbProject = (Project) session.get(Project.class, projectId);
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
			return dbProject;
		} catch (Exception e) {
			throw new HibernateException("record not updated, something went wrong");
		} finally {
			session.close();
		}
	}
	
	public List<Employee> assigned(long projectId) {
		Session session = HibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		List<Employee> empResult=null;
		try {
			Query query = session.createQuery("select employee from ProjectEmployee where project_id = :project_id");
			query.setParameter("project_id", projectId);
			empResult=query.list();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
		return empResult;
	}

	public ProjectEmployee assign(Project project) {
		Session session = HibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		project.setEmployees(null);
		session.save(project);
		List<Integer> empIds = project.getEmpIds();
		ProjectEmployee projectEmployee =new ProjectEmployee();
		for (Integer empId : empIds) {
			try {
				Employee employee = (Employee) session.get(Employee.class, (long) empId);
				if(employee!=null){
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
