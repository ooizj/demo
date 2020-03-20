package me.ooi.demo.testjpa_hibernate;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.ejb.HibernatePersistence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import me.ooi.demo.testjpa_hibernate.po.User;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestJPAWithoutPersistenceXML {
	
	private static class MyPersistenceUnitInfo implements PersistenceUnitInfo {
		
		private PersistenceUnitTransactionType transactionType ; 
		public MyPersistenceUnitInfo(PersistenceUnitTransactionType transactionType) {
			this.transactionType = transactionType ; 
		}
		@Override
		public ValidationMode getValidationMode() {
			return null;
		}
		@Override
		public PersistenceUnitTransactionType getTransactionType() {
			return transactionType ; 
		}
		@Override
		public SharedCacheMode getSharedCacheMode() {
			return null;
		}
		@Override
		public Properties getProperties() {
			return new Properties() ; 
		}
		@Override
		public String getPersistenceXMLSchemaVersion() {
			return null;
		}
		@Override
		public URL getPersistenceUnitRootUrl() {
			return null;
		}
		@Override
		public String getPersistenceUnitName() {
			return "poi" ; 
		}
		@Override
		public String getPersistenceProviderClassName() {
			return HibernatePersistence.class.getName() ; 
		}
		@Override
		public DataSource getNonJtaDataSource() {
			return null;
		}
		@Override
		public ClassLoader getNewTempClassLoader() {
			return null;
		}
		@Override
		public List<String> getMappingFileNames() {
			List<String> mappingFiles = new ArrayList<String>() ;
			mappingFiles.add("me/ooi/demo/testjpa_hibernate/po/User.hbm.xml") ; 
			return mappingFiles ; 
		}
		@Override
		public List<String> getManagedClassNames() {
			List<String> classes = new ArrayList<String>() ;
			classes.add("me.ooi.demo.testjpa_hibernate.po.Project") ; 
			return classes ; 
		}
		@Override
		public DataSource getJtaDataSource() {
			return null;
		}
		@Override
		public List<URL> getJarFileUrls() {
			return Collections.emptyList() ; 
		}
		@Override
		public ClassLoader getClassLoader() {
			return Thread.currentThread().getContextClassLoader() ; 
		}
		@Override
		public boolean excludeUnlistedClasses() {
			return false;
		}
		@Override
		public void addTransformer(ClassTransformer transformer) {
		}
	}
	
	private EntityManagerFactory emf ;
	
	@Before
	public void init(){
		PersistenceProvider provider = new HibernatePersistence() ; 
		Map<String, Object> properties = new HashMap<>() ; 
		properties.put(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQLDialect") ;
		properties.put(AvailableSettings.DRIVER, "com.mysql.jdbc.Driver") ; 
		properties.put(AvailableSettings.URL, "jdbc:mysql://127.0.0.1:3306/mytest?useUnicode=true&characterEncoding=utf8&autoReconnect=true&pinGlobalTxToPhysicalConnection=true&useSSL=false") ;
		properties.put(AvailableSettings.USER, "root") ; 
		properties.put(AvailableSettings.PASS, "root") ; 
		properties.put(AvailableSettings.TRANSACTION_STRATEGY, "org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory") ;
		properties.put(AvailableSettings.SHOW_SQL, "true") ; 
		properties.put(AvailableSettings.HBM2DDL_AUTO, "none") ; 
		
		emf = provider.createContainerEntityManagerFactory(
				new MyPersistenceUnitInfo(PersistenceUnitTransactionType.RESOURCE_LOCAL), properties) ;
	}
	
	@After
	public void destroy(){
		if( emf != null ){
			emf.close();
		}
	}
	
	@Test
	public void t1(){
		EntityManager em = emf.createEntityManager() ; 
		EntityTransaction tx = em.getTransaction() ; 
		try {
			
			tx.begin(); //start transaction

			//test save
			User u = new User() ; 
			u.setAge(1);
			u.setName("json");
			em.persist(u) ; 
			
			//test select
			System.out.println(em.createQuery("select u from User u").getResultList());
			System.out.println(em.createQuery("select p from Project p").getResultList());
			
			tx.commit(); //commit transaction
		} catch (Exception e) {
			tx.rollback(); //rollback transaction
			e.printStackTrace();
		}
		
		em.close();
		
	}

}
