package com.synerzip.projectmanagementapp.serviceimplementation;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.mysql.jdbc.StringUtils;
import com.synerzip.projectmanagementapp.dbconnection.EmployeeHibernateUtils;
import com.synerzip.projectmanagementapp.dbconnection.ProjectHibernateUtils;
import com.synerzip.projectmanagementapp.model.Employee;
import com.synerzip.projectmanagementapp.model.Project;
import com.synerzip.projectmanagementapp.model.Project_Employee;
import com.synerzip.projectmanagementapp.services.ProjectServices;

public class ProjectServiceImplementation implements ProjectServices {
	public Project getProject(long projectId) {
		Session session = ProjectHibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		Project project = new Project();
		try {
			project = (Project) session.get(Project.class, projectId);
			project.setProject_employees(null);
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
			Query query = session.createQuery("from com.synerzip.projectmanagementapp.model.Project");
			query.setFirstResult(start);
			query.setMaxResults(size);
			List<Project> projects = query.list();
			return projects;
		} catch (Exception e) {
			return null;
		}
	}

	public Project addProject(Project project) {

		Session session = ProjectHibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();

		try {
			session.save(project);
			addProjectEmployee(project);
			tx.commit();
			return project;
		} catch (Exception e) {
			return null;
		} finally {
			session.close();
		}
	}

	public void addProjectEmployee(Project project) {
		
		List<Integer> empIds=project.getEmp_id();
		for(Integer empId :empIds){
			try {
				Session session = EmployeeHibernateUtils.getSession();
				Session sessionPE = EmployeeHibernateUtils.getSession();
				org.hibernate.Transaction tx = sessionPE.beginTransaction();
				Employee employee= (Employee) session.get(Employee.class, (long) empId);
				Project_Employee pe=new Project_Employee();
				pe.setEmployee(employee);
				pe.setProject(project);
				sessionPE.save(pe);
				sessionPE.flush();
				tx.commit();
			}catch(Exception exception){
				exception.printStackTrace();
			}
		}
	}

	public String deleteProject(long projectId) {

		Session session = ProjectHibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();

		try {
			String deleteQuery = "FROM Project WHERE project_id = :project_id";
			Query query = session.createQuery(deleteQuery);
			query.setParameter("project_id", projectId);
			Project project=(Project)query.list().get(0);
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
		Session session = ProjectHibernateUtils.getSession();
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
		Session session = EmployeeHibernateUtils.getSession();
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
		Session session = EmployeeHibernateUtils.getSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		try {
			Project dbProject=(Project)session.get(Project.class, projectId);
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
