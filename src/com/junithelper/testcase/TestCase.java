package com.junithelper.testcase;

public class TestCase {

	private String testCaseName;
	private Expectation expectation;
	private Execution execution;
	private Verification verification;
	
	public Expectation getExpectation() {
		return expectation;
	}
	public void setExpectation(Expectation expectation) {
		this.expectation = expectation;
	}
	public Execution getExecution() {
		return execution;
	}
	public void setExecution(Execution execution) {
		this.execution = execution;
	}
	public Verification getVerification() {
		return verification;
	}
	public void setVerification(Verification verification) {
		this.verification = verification;
	}
	public String getTestCaseName() {
		return testCaseName;
	}
	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}	
}
