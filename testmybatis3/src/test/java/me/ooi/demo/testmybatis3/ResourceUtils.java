package me.ooi.demo.testmybatis3;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * 资源工具
 * @author jun.zhao
 * @since 1.0
 */
public class ResourceUtils {
	
	private static Logger LOG = LogManager.getLogger(ResourceUtils.class) ; 
	
	public static Properties getPropertiesFromClassPath(String classPath){
		Properties prop = new Properties() ; 
		InputStream is = getInputStreamFromClassPath(classPath) ; 
		try {
			prop.load(is) ; 
			return prop ; 
		} catch (IOException e) {
			LOG.error(e.getMessage(), e) ; 
			return null ; 
		}
	}
	
	public static InputStream getInputStreamFromClassPath(String classPath){
		return ResourceUtils.class.getResourceAsStream(classPath) ; 
	}

}
