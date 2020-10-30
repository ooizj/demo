package me.ooi.demo.testjdbc;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

/**
 * @author jun.zhao
 */
public class CommentsReader {
	
	private File file;

	public CommentsReader(File file) {
		super();
		this.file = file;
	}
	
	public Map<String, String> read() throws IOException, URISyntaxException {
		Map<String, String> ret = new LinkedHashMap<String, String>();
		List<String> lines = FileUtils.readLines(file, "utf-8");
		for (String line : lines) {
			if( line == null || "".equals(line.trim()) ) {
				continue;
			}
			
			String[] items = line.split("\\t");
			ret.put(items[1], items[0]);
		}
		return ret;
	}

}
