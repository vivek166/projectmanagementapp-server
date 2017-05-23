package com.synerzip.projectmanagementapp.dbconnection;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class HibernateUtils {

	static final Logger logger = Logger.getLogger(HibernateUtils.class);
	static Configuration con;
	static ServiceRegistry registry;
	static SessionFactory sf;
	static {
		logger.info("started initialising hibernate config");
		con = new Configuration().configure();
		registry = new ServiceRegistryBuilder().applySettings(con.getProperties()).buildServiceRegistry();
		sf = con.buildSessionFactory(registry);
		logger.info("completed with hibernate config init");
	}

	public static Session getSession() {
		return sf.openSession();

	}
}