package com.synerzip.projectmanagementapp.serviceimplementation;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.synerzip.projectmanagementapp.dbconnection.EmployeeHibernateUtils;
import com.synerzip.projectmanagementapp.dbconnection.ProjectHibernateUtils;
import com.synerzip.projectmanagementapp.model.Employee;
import com.synerzip.projectmanagementapp.model.Project;
import com.synerzip.projectmanagementapp.services.ProjectServices;

public class ProjectServiceImplementation implements ProjectServices {
	public Project getProject(long projectId) {
		Session session = ProjectHibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		Project project=new Project();
		try {
			project = (Project) session.get(Project.class, projectId);
			project.setEmployees(null);
			tx.commit();
			return project;
		} catch (Exception e) {
			return null;
		} finally {
			session.close();
		}
	}

	public List<Project> getProjects(int start, int size) {
		Session session = ProjectHibernateUtils.getSession();
		session.beginTransaction();
		try {
			Query query = session
					.createQuery("from com.synerzip.projectmanagementapp.model.Project");
			query.setFirstResult(start);
			query.setMaxResults(size);
			List<Project> projects = query.list();
			return projects;
		} catch (Exception e) {
			return null;
		}finally {
			session.close();
		}
	}

	public Project addProject(Project project) {

		Session session = ProjectHibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();

		try {
			session.save(project);
			tx.commit();
			return project;
		} catch (Exception e) {
			return null;
		} finally {
			session.close();
		}
	}

	public String deleteProject(long projectId) {

		Session session = ProjectHibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();

		try {
			String deleteQuery = "DELETE FROM Project WHERE project_id = :project_id";
			Query query = session.createQuery(deleteQuery);
			query.setParameter("project_id", projectId);
			query.executeUpdate();
			tx.commit();
		} catch (Exception e) {
			return null;
		} finally {
			session.close();
		}
		return "record deleted";
	}

	public Project updateProject(Project project, long projectId) {
		Session session = ProjectHibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();

		try {
			String deleteQuery = "DELETE FROM Project WHERE project_id = :project_id";
			Query query = session.createQuery(deleteQuery);
			query.setParameter("project_id", projectId);
			query.executeUpdate();
			session.save(project);
			tx.commit();
			return project;
		} catch (Exception e) {
			return null;
		} finally {
			session.close();
		}
	}

	public List<Employee> getProjectEmployees(long projectId) {
		Session session = EmployeeHibernateUtils.getSession();
		org.hibernate.Transaction tx =session.beginTransaction();
		try {
			Query query = session.createQuery("from com.synerzip.projectmanagementapp.model.Employee e join project_employee pe on e.emp_id=pe.employees_emp_id "
					+ "WHERE pe.project_project_id = :project_id");
			query.setParameter("project_id", projectId);
			List<Employee> listResult = query.list();
			return listResult;
		} catch (Exception e) {
			return null;
		}finally {
			session.close();
		}
	}
}
