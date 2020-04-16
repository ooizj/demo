package me.ooi.demo.testmybatis3.plugin;

import java.sql.Statement;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;

/**
 * MyBatis 允许你在映射语句执行过程中的某一点进行拦截调用。默认情况下，MyBatis 允许使用插件来拦截的方法调用包括：
 * 
 * Executor (update, query, flushStatements, commit, rollback, getTransaction, close, isClosed)
 * ParameterHandler (getParameterObject, setParameters)
 * ResultSetHandler (handleResultSets, handleOutputParameters)
 * StatementHandler (prepare, parameterize, batch, update, query)
 * 
 * @author jun.zhao
 * @since 1.0
 */
@Intercepts({
	@Signature(type = StatementHandler.class, method = "update", args = { 
			Statement.class}),
	@Signature(type = StatementHandler.class, method = "query", args = { 
			Statement.class, ResultHandler.class})
	})
public class PrintSqlPlugin implements Interceptor {
	
	private Properties properties = new Properties();

	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget() ; 
	    String sql = statementHandler.getBoundSql().getSql() ; 
		System.out.println("执行sql："+sql);
	    return invocation.proceed() ; 
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
		System.out.println(this.properties);
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}
}