package me.ooi.demo.testactiviti710_springboot;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import oracle.jdbc.pool.OracleDataSource;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestActiviti710 {

	@Test
	public void t1() throws SQLException {
		
//		JdbcDataSource ds = new JdbcDataSource();
//		ds.setUrl("jdbc:h2:~/test");
//		ds.setUser("sa");
		
//		MysqlDataSource ds = new MysqlDataSource();
//		ds.setUrl("jdbc:mysql://127.0.0.1:3306/testjbpm630_1?useUnicode=true&characterEncoding=utf8&autoReconnect=true&pinGlobalTxToPhysicalConnection=true&useSSL=false&serverTimezone=GMT");
//		ds.setUser("root");
//		ds.setPassword("root");
		
		OracleDataSource ds = new OracleDataSource();
		ds.setURL("jdbc:oracle:thin:@192.168.0.167:1521:ORCL");
		ds.setUser("testjbpm");
		ds.setPassword("testjbpm");
		
		// Create Activiti process engine
		ProcessEngine processEngine = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration()
				.setDataSource(ds)
				.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
//				.setDatabaseTablePrefix("P1_")
				.buildProcessEngine();

		// Get Activiti services
		RepositoryService repositoryService = processEngine.getRepositoryService();
		RuntimeService runtimeService = processEngine.getRuntimeService();

		// Deploy the process definition
		repositoryService.createDeployment().addClasspathResource("t2.bpmn").deploy();

		// Start a process instance
		Map<String, Object> params = new HashMap<>() ; 
		params.put("userId", "123") ; 
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("t2", params);
        System.out.println(processInstance);
	}

}
