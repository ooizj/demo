package me.ooi.demo.testjdbc;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class T1 {
	
	@Test
	public void t1() {
		System.out.println(String.class.getName());
		
		System.out.println(EntityUtils.dbFieldToBeanField("test_data"));
	}
	
	@Test
	public void t2() throws URISyntaxException, IOException {
		URL url = this.getClass().getResource("/comments.txt");
		System.out.println(new CommentsReader(new File(url.toURI())).read());
	}
	
}
