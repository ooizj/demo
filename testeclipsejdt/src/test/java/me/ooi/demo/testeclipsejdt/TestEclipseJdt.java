package me.ooi.demo.testeclipsejdt;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.junit.Before;
import org.junit.Test;

/**
 * @author jun.zhao
 */
public class TestEclipseJdt {
	
	private String javaCode;
	private CompilationUnit compilationUnit;
	
	@Before
	public void init() throws IOException {
		javaCode = IOUtils.toString(TestEclipseJdt.class.getResourceAsStream("/T1.java.txt"), "utf-8");
		
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(javaCode.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		compilationUnit = (CompilationUnit) parser.createAST(null);
	}
	
	private String getComment(Javadoc node) {
		final Pattern COMMENT_PATTERN = Pattern.compile(
				"/\\*\\*\r\n" + 
				"[\\s]+\\*([^\\r]*)\r\n" + 
				"[\\s]+\\*/"
				);
		
		int start = node.getStartPosition();
		int end = start + node.getLength();
		String comment = javaCode.substring(start, end);
		Matcher m = COMMENT_PATTERN.matcher(comment);
		if( m.find() ) {
			return m.group(1).trim();
		}else {
			throw new RuntimeException("loss comment");
		}
	}
	
	@Test
	public void t1() {
		
		compilationUnit.accept(new ASTVisitor() {
			@Override
			public boolean visit(FieldDeclaration node) {
				String comment = getComment(node.getJavadoc());
				System.out.print(comment+"\t");
				return super.visit(node);
			}
		});
		
		System.out.println();
		compilationUnit.accept(new ASTVisitor() {
			@Override
			public boolean visit(FieldDeclaration node) {
				String fieldName = node.fragments().get(0).toString();
				System.out.print("{."+fieldName+"}\t");
				return super.visit(node);
			}
		});
	}
	
	@Test
	public void t2() {
		compilationUnit.accept(new ASTVisitor() {
			@Override
			public boolean visit(FieldDeclaration node) {
				String fieldName = node.fragments().get(0).toString();
				String comment = getComment(node.getJavadoc());
				System.out.println(comment+"\t"+fieldName);
				return super.visit(node);
			}
		});
	}

}
