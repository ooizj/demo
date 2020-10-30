package me.ooi.demo.testjdbc;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

/**
 * @author jun.zhao
 */
public class CommentUtils {
	
	private static Map<String, String> commentMap;
	static {
		URL url = CommentUtils.class.getResource("/comments.txt");
		try {
			commentMap = (new CommentsReader(new File(url.toURI())).read());
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public static String getComment(String name) {
		return commentMap.get(name);
	}

}
