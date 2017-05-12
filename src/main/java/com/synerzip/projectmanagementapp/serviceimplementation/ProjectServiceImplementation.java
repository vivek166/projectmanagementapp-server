package com.synerzip.projectmanagementapp.serviceimplementation;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.hibernate.Query;
import org.hibernate.Session;
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
	public Project getProject(long projectId) {
		Session session = HibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		Project project = new Project();
		try {
			project = (Project) session.get(Project.class, projectId);
			//project.setProjectEmployees(null);
			tx.commit();
			return project;
		} catch (Exception e) {
			return null;
		} finally {
			session.close();
		}
	}

	public PageResult getProjects(int start, int size, String content) {
		Session session = HibernateUtils.getSession();
		session.getTransaction().begin();
		if (org.apache.commons.lang.StringUtils.isEmpty(content)) {
			try {
				Query query = session.createQuery("from com.synerzip.projectmanagementapp.model.Project");
				query.setFirstResult(start);
				query.setMaxResults(size);
				List<Project> projects = query.list();
				int count = ((Long) session
						.createQuery("select count(*) from com.synerzip.projectmanagementapp.model.Project")
						.uniqueResult()).intValue();
				session.flush();
				session.getTransaction().commit();
				PageResult pageResults = new PageResult();
				pageResults.setData(projects);
				pageResults.setTotalResult(count);
				return pageResults;
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				session.close();
			}
		} else {
			return searchProject(start, size, content);
		}
		session.getTransaction().commit();
		return null;
	}

	public PageResult searchProject(int start, int size, String content) {
		EntityManager entityManager = Persistence.createEntityManagerFactory("MumzHibernateSearch")
				.createEntityManager();
		entityManager.getTransaction().begin();
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		try {
			// fullTextEntityManager.createIndexer().startAndWait();
			QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Project.class)
					.get();
			org.apache.lucene.search.Query query = qb.keyword()
					.onFields("projectId", "technologyUsed", "projectFeature", "projectDescription").matching(content)
					.createQuery();
			javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(query, Project.class);
			jpaQuery.setFirstResult(start);
			jpaQuery.setMaxResults(size);
			List<Project> projectResult = jpaQuery.getResultList();
			if (projectResult != null) {
				PageResult pageResults = new PageResult();
				pageResults.setData(projectResult);
				pageResults.setTotalResult(0);
				return pageResults;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fullTextEntityManager != null) {
				fullTextEntityManager.close();
			}
			fullTextEntityManager = null;
		}
		entityManager.getTransaction().commit();
		return null;
	}

	public Project addProject(Project project) {

		Session session = HibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();

		try {
			session.save(project);
			// addProjectEmployee(project);
			tx.commit();
			return project;
		} catch (Exception e) {
			return null;
		} finally {
			session.close();
		}
	}

	public void addProjectEmployee(Project project) {
		Session session = HibernateUtils.getSession();
		Session sessionPE = HibernateUtils.getSession();
		org.hibernate.Transaction tx = sessionPE.beginTransaction();
		List<Integer> empIds = project.getEmpIds();
		for (Integer empId : empIds) {
			try {
				Employee employee = (Employee) session.get(Employee.class, (long) empId);
				ProjectEmployee pe = new ProjectEmployee();
				pe.setEmployee(employee);
				pe.setProject(project);
				sessionPE.save(pe);
				sessionPE.flush();
				tx.commit();
			} catch (Exception exception) {
				exception.printStackTrace();
			} finally {
				sessionPE.close();
			}
		}
	}

	public String deleteProject(long projectId) {

		Session session = HibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();

		try {
			String deleteQuery = "FROM Project WHERE project_id = :project_id";
			Query query = session.createQuery(deleteQuery);
			query.setParameter("project_id", projectId);
			Project project = (Project) query.list().get(0);
			session.delete(project);
			session.flush();
			tx.commit();
		} catch (Exception e) {
			return null;
		} finally {
			session.close();
		}
		return "record deleted";
	}

	public Project updateProject(Project project, long projectId) {
		Session session = HibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			session.get(Project.class, projectId);
			session.update(project);
			tx.commit();
			return project;
		} catch (Exception e) {
			return null;
		} finally {
			session.close();
		}
	}

	public List<Employee> getProjectEmployees(long projectId) {
		Session session = HibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			Query query = session.createQuery(
					"from com.synerzip.projectmanagementapp.model.Employee e join project_employee pe on e.emp_id=pe.employees_emp_id "
							+ "WHERE pe.project_project_id = :project_id");
			query.setParameter("project_id", projectId);
			List<Employee> listResult = query.list();
			return listResult;
		} catch (Exception e) {
			return null;
		} finally {
			session.close();
		}
	}

	public Project updateProjectPartially(Project project, long projectId) {
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
			return null;
		} finally {
			session.close();
		}
	}

}
