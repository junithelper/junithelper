package com.junithelper.testcase;
import java.util.List;
import java.util.Map;

public class Expectation {
	
	private List<Map<String, List<Object>>> statements;

	public List<Map<String, List<Object>>> getStatements() {
		return statements;
	}

	public void setStatements(List<Map<String, List<Object>>> statements) {
		this.statements = statements;
	}
}
