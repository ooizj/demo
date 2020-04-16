package me.ooi.demo.testmybatis3.plugin;

import java.util.Properties;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

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
	@Signature(type = Executor.class, method = "query", args = { 
			MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
	@Signature(type = Executor.class, method = "query", args = { 
			MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
	})
public class ExamplePlugin2 implements Interceptor {
	
	private Properties properties = new Properties();

	@SuppressWarnings("rawtypes")
	public Object intercept(Invocation invocation) throws Throwable {
		Executor executor = (Executor) invocation.getTarget() ; 
		Object[] args = invocation.getArgs() ; 
		
		if( args.length == 6 ){
			BoundSql boundSql = (BoundSql) args[args.length-1] ; 
			String sql = boundSql.getSql() ; 
			System.out.println("执行sql："+sql);
			return invocation.proceed();
		}else {
			MappedStatement ms = (MappedStatement) args[0] ; 
			Object parameter = args[1] ; 
			RowBounds rowBounds = (RowBounds) args[2] ; 
			ResultHandler resultHandler = (ResultHandler) args[3] ; 
			
			BoundSql boundSql = ms.getBoundSql(parameter);
		    CacheKey cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
		    
		    String sql = boundSql.getSql() ; 
			System.out.println("执行sql："+sql);
		    
		    return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql) ; 
		}
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