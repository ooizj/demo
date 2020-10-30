package me.ooi.demo.testjdbc;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import freemarker.core.ParseException;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateNotFoundException;


/**
 * @author jun.zhao
 */
public class TestGenerateCode {
	
	private Configuration cfg;
	
	@Before
	public void init() throws IOException, URISyntaxException {
		// Create your Configuration instance, and specify if up to what FreeMarker
		// version (here 2.3.29) do you want to apply the fixes that are not 100%
		// backward-compatible. See the Configuration JavaDoc for details.
		cfg = new Configuration(Configuration.VERSION_2_3_30);

		// Specify the source where the template files come from. Here I set a
		// plain directory for it, but non-file-system sources are possible too:
		URL url = this.getClass().getResource("/templates");
		cfg.setDirectoryForTemplateLoading(new File(url.toURI()));

		// From here we will set the settings recommended for new projects. These
		// aren't the defaults for backward compatibilty.

		// Set the preferred charset template files are stored in. UTF-8 is
		// a good choice in most applications:
		cfg.setDefaultEncoding("UTF-8");

		// Sets how errors will appear.
		// During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		// Don't log exceptions inside FreeMarker that it will thrown at you anyway:
		cfg.setLogTemplateExceptions(false);

		// Wrap unchecked exceptions thrown during template processing into TemplateException-s:
		cfg.setWrapUncheckedExceptions(true);

		// Do not fall back to higher scopes when reading a null loop variable:
		cfg.setFallbackOnNullLoopVariable(false);
	}
	
	@Test
	public void t1() throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
		// Create the root hash. We use a Map here, but it could be a JavaBean too.
		Map<String, Object> root = new HashMap<>();

		// Put string "user" into the root
		root.put("user", "Big Joe");

		Template temp = cfg.getTemplate("test.ftlh");
		
		Writer out = new OutputStreamWriter(System.out);
		temp.process(root, out);
	}
	
	@Test
	public void t2() throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException, ClassNotFoundException, SQLException {
		
		Template temp = cfg.getTemplate("po.ftlh");
		
		Map<String, Object> root = new HashMap<>();
		root.put("date", new SimpleDateFormat("yyyy年MM月dd").format(new Date()));
		root.put("name", "Hello");
		
		Map<String, Class<?>> fieldMap = TestJdbc.getTestFieldMap1();
		root.put("fieldMap", fieldMap);
		
		BeansWrapper wrapper = new BeansWrapperBuilder(Configuration.VERSION_2_3_30).build();
		TemplateHashModel staticModels = wrapper.getStaticModels();
		root.put("EntityUtils", staticModels.get(EntityUtils.class.getName()));
		root.put("CommentUtils", staticModels.get(CommentUtils.class.getName()));
		root.put("TypeMapping", staticModels.get(TypeMapping.class.getName()));
		
		Writer out = new OutputStreamWriter(System.out);
		temp.process(root, out);
	}
	
}
