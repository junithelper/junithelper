package com.junithelper.testcase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.type.Type;

public class Method {

	private String name;
	private String modifier;
	private String retrunType;
	private boolean isPrivate;
	private boolean isStatic;
	private Type type;
	private List<TestCase> testCases = new ArrayList<TestCase>();
	private Map<String,String> paramMap; 
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public String getRetrunType() {
		return retrunType;
	}
	public void setRetrunType(String retrunType) {
		this.retrunType = retrunType;
	}
	public boolean isPrivate() {
		return isPrivate;
	}
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}
	public boolean isStatic() {
		return isStatic;
	}
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
	public List<TestCase> getTestCases() {
		return testCases;
	}
	public void setTestCases(List<TestCase> testCases) {
		this.testCases = testCases;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public Map<String, String> getParamMap() {
		return paramMap;
	}
	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}
	
	

}
