package me.ooi.demo.testhibernate420;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class HibernateUtils {

    private static final SessionFactory sessionFactory = buildSessionFactory();
    private HibernateUtils() {
	}

    private static SessionFactory buildSessionFactory() {
        try {
        	
        	Configuration cfg = new Configuration() ; 
        	cfg.configure("hibernate.cfg.xml") ; 
        	
        	ServiceRegistry sr = new ServiceRegistryBuilder().applySettings(cfg.getProperties()).buildServiceRegistry() ; 
        	
//        	Object obj = sr.getService(TransactionFactory.class) ; 
//        	System.out.println(obj);
        	
        	return cfg.buildSessionFactory(sr) ; 
        }
        catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
}