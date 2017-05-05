package com.synerzip.projectmanagementapp.dbconnection;

import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.synerzip.projectmanagementapp.model.Project_Employee;

public class Project_EmployeeHibernateUtils {

	static Configuration con;
	static ServiceRegistry registry;
	static SessionFactory sf;
	static {
		System.out.println("started initialising hibernate config");
		con = new Configuration().configure().addAnnotatedClass(Project_Employee.class);
		registry = new ServiceRegistryBuilder().applySettings(
				con.getProperties()).buildServiceRegistry();
		sf = con.buildSessionFactory(registry);
		System.out.println("completed with hibernate config init");
	}

	public static Session getSession() {
		return sf.openSession();

	}
}