package com.junithelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.expr.Instanceof;

import javax.lang.model.element.Modifier;

import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.builders.AnnotatedBuilder;
import org.junit.runner.RunWith;
import org.mockito.configuration.IMockitoConfiguration;
import org.mockito.internal.matchers.VarargMatcher;
import org.omg.CosNaming.IstringHelper;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.TypeDeclarationStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.junithelper.testcase.Execution;
import com.junithelper.testcase.Expectation;
import com.junithelper.testcase.Method;
import com.junithelper.testcase.TestCase;
import com.junithelper.testcase.Verification;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;


public class JunitHelper {

	private static List<ImportDeclaration> imports;
	private List<TypeDeclaration> types;
	private static PackageDeclaration packageDeclartion;
	private static String className;
	private List<MethodSpec> methods;
	private static List<MethodDeclaration> methodDeclarations = new ArrayList<MethodDeclaration>();
	private List<TestCase> testCases = new ArrayList<TestCase>();
	private String methodName;
	private static List<MethodSpec> testMethods = new ArrayList<MethodSpec>();
	private static Map<Class,String> staticImports = new HashMap<Class, String>();
	private static boolean isSingleTon;


	public static void main(String[] args) throws IOException, ParseException {

		JunitHelper junitHelper = new JunitHelper();
		File file = new File("E:/");
		FileInputStream in = new FileInputStream(file);


		CompilationUnit cu;
		try {
			// parse the file
			cu = JavaParser.parse(in);
		} finally {
			in.close();
		}



		isSingleTon = false;

		className = file.getName();
		className = className.split("\\.")[0];
		System.out.println("ClassName:"+className);
		imports = cu.getImports();


		packageDeclartion = cu.getPackage(); 

		className = file.getName();
		className = className.split("\\.")[0];

		List<TypeDeclaration> types = cu.getTypes();


		/*for(ImportDeclaration imp:imports)
        {
        	//System.out.println(imp.getName());
        }*/

		for(TypeDeclaration type:types)
		{
			if(type instanceof ClassOrInterfaceDeclaration && !(((ClassOrInterfaceDeclaration) type).isInterface()))
			{

				List<BodyDeclaration> bodyDeclaration = type.getMembers();
				for(BodyDeclaration body:bodyDeclaration)
				{
					if(body instanceof ClassOrInterfaceDeclaration)
					{
						ClassOrInterfaceDeclaration classOrInterface =(ClassOrInterfaceDeclaration) body;
						//System.out.println();

					}

					if(body instanceof FieldDeclaration )
					{
						//doSomething((FieldDeclaration) body);
					}
					else if(body instanceof ConstructorDeclaration )
					{
						//processConstructor((ConstructorDeclaration) body);
						ConstructorDeclaration constructorDeclartion = (ConstructorDeclaration) body;
						if(constructorDeclartion.getModifiers() == 2)
						{
							isSingleTon = true;
						}
					}

					else if(body instanceof MethodDeclaration )
					{
						methodDeclarations.add((MethodDeclaration) body);
						//						junitHelper.analyzeMethod(cu, file,isSingleTon, (MethodDeclaration) body);
					}

				}
			}
		}

		if(methodDeclarations.size()>0)
		{
			for(MethodDeclaration methodDeclaration:methodDeclarations)
			{
				junitHelper.analyzeMethod(cu, file,isSingleTon, methodDeclaration);
			}
		}



		TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(className+"Test")
				.addModifiers(Modifier.PUBLIC)
				.addAnnotation(AnnotationSpec.builder(RunWith.class).addMember("value","$T.$L",PowerMockRunner.class,"class").build());

		for(MethodSpec methodSpec:testMethods)
		{
			typeSpecBuilder.addMethod(methodSpec);
			typeSpecBuilder.build();
		}


		TypeSpec testClass = typeSpecBuilder.build();

		JavaFile javaFile = JavaFile.builder("com", testClass)
				.addStaticImport(Assert.class, "assertEquals")
				.build();

		javaFile.writeTo(new File("E:/Workarea/JavaParser/Files/"));


		// new ConditionalExpressionVistor().visit(cu, null);
		/* 
        MethodSpec main = MethodSpec.methodBuilder("main")
        	    .addCode(""
        	            + "int total = 0;\n"
        	            + "for (int i = 0; i < 10; i++) {\n"
        	            + "  total += i;\n"
        	            + "}\n")
        	            .addModifiers(Modifier.PUBLIC)
        	    .returns(void.class)
        	        .build();*/



	}

	private static class MethodVisitor extends VoidVisitorAdapter<Object> {

		@Override
		public void visit(MethodDeclaration n, Object arg) {
			// here you can access the attributes of the method.
			// this method will be called for all methods in this
			// CompilationUnit, including inner class methods
			// System.out.println(n.getName());

			if (n.getName().equals("getEnterpriseCustomer")) {
				System.out.println("yeah I found");
				n.accept(new ConditionalExpressionVistor(), arg);

			}

			super.visit(n, arg);
		}

	}

	private static class ConditionalExpressionVistor extends
	VoidVisitorAdapter<Object> {

		@Override
		public void visit(ConditionalExpr n, Object arg) {
			// here you can access the attributes of the method.
			// this method will be called for all methods in this
			// CompilationUnit, including inner class methods
			// System.out.println(n.getName());
			System.out.println("Hi....");
			System.out.println(n.getCondition());

			super.visit(n, arg);
		}

	}

	private  void analyzeMethod(CompilationUnit cu,File file,boolean isSingleTon,MethodDeclaration methodDeclaration) throws IOException {



		int methodModifier = methodDeclaration.getModifiers();
		String modifier = null;
		Method method = new Method();

		method.setName(methodDeclaration.getName());


		methodName = methodDeclaration.getName();


		if(methodModifier == 1)
		{
			modifier = "public";
			method.setPrivate(false);
			method.setStatic(false);
		}
		else if(methodModifier == 2)
		{
			modifier = "private";
			method.setPrivate(true);
			method.setStatic(false);
		}
		else if(methodModifier == 4)
		{
			modifier = "protected";
			method.setPrivate(false);
			method.setStatic(false);
		}
		else if(methodModifier == 8)
		{
			modifier = "static";
			method.setPrivate(false);
			method.setStatic(true);
		}
		else if(methodModifier == 9)
		{
			modifier = "public static";
			method.setPrivate(false);
			method.setStatic(true);
		}
		else if(methodModifier == 10)
		{
			modifier = "private static";
			method.setPrivate(true);
			method.setStatic(false);
		}
		else if(methodModifier == 12)
		{
			modifier = "protected static";
			method.setPrivate(false);
			method.setStatic(true);
		}

		method.setModifier(modifier);

		method.setType(methodDeclaration.getType());



		List<TestCase> testCasesForMethods = new ArrayList<TestCase>();

		if(method.getName().equalsIgnoreCase("getInstance") && isSingleTon)
		{

			TestCase testCase = new TestCase();
			testCase.setTestCaseName("test_"+method.getName());
			Expectation expectation = new Expectation();
			Execution execution = null;
			Verification verification = new Verification();
			//.addStatement("$T "+variable+"One = $T.getInstance()",instanceOne,instanceOne)
			List<Map<String, List<Object>>> statements = new ArrayList<Map<String,List<Object>>>();
			ClassName instance= ClassName.get(cu.getPackage().getName().toString(), methodDeclaration.getType().toString());
			String statementOne = "$T instanceOne = $T."+method.getName()+"()";
			List<Object> objectForStatementOne = new ArrayList<Object>();
			objectForStatementOne.add(instance);
			objectForStatementOne.add(instance);
			Map<String,List<Object>> statementMap = new HashMap<String, List<Object>>();
			statementMap.put(statementOne, objectForStatementOne);
			statements.add(statementMap);

			String statementTwo = "$T instanceTwo = $T."+method.getName()+"()";
			List<Object> objectForStatementTwo = new ArrayList<Object>();
			objectForStatementTwo.add(instance);
			objectForStatementTwo.add(instance);
			Map<String,List<Object>> statementMapTwo = new HashMap<String, List<Object>>();
			statementMapTwo.put(statementTwo, objectForStatementTwo);
			statements.add(statementMapTwo);



			List<Map<String, List<Object>>> verificationStatements = new ArrayList<Map<String,List<Object>>>();
			String assertStatement = "assertEquals(instanceOne,instanceTwo)";
			List<Object> assertList = null;
			Map<String,List<Object>> assertMap = new HashMap<String, List<Object>>();
			assertMap.put(assertStatement, assertList);
			verificationStatements.add(assertMap);
			verification.setStatements(verificationStatements);

			expectation.setStatements(statements);
			testCase.setExpectation(expectation);
			testCase.setExecution(execution);
			testCase.setVerification(verification);
			testCasesForMethods.add(testCase);

		}
		else
		{
			List<Parameter> parameter = methodDeclaration.getParameters();
			if(parameter!=null && parameter.size()>0)
			{
				Map<String,String> paramMap = new HashMap<String, String>();
				for(Parameter param:parameter)
				{

					String resolveImport = resolveImportsForType(param.getType().toString(), cu.getImports(), cu.getPackage(), className);
					paramMap.put(resolveImport, param.getId().toString());
				}

				method.setParamMap(paramMap);
			}

			if(isMultipleTestScenarios(methodDeclaration.getBody().getStmts(),method))
			{

			}
			else{
				TestCase testCase =processStatements(methodDeclaration.getBody().getStmts(), method);
				if(testCase!=null)testCasesForMethods.add(testCase);

			}

		}

		method.setTestCases(testCasesForMethods);


		if(method.getTestCases()!=null)
		{



			for(TestCase test:method.getTestCases() )

			{
				
				
				MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(test.getTestCaseName())
						.addModifiers(Modifier.PUBLIC)
						.returns(void.class)
						.addAnnotation(Test.class);

				if(test.getExpectation()!=null)
				{
					List<Map<String, List<Object>>> expectationStatements = test.getExpectation().getStatements();
					if(expectationStatements!=null && expectationStatements.size()>0)
					{
						for(Map<String, List<Object>> expMap:expectationStatements)
						{
							for(Map.Entry<String, List<Object>> expectationEntry:expMap.entrySet())
							{
								String format = expectationEntry.getKey();

								Object[] objAargs = new Object [expectationEntry.getValue().size()];
								expectationEntry.getValue().toArray(objAargs);
								methodSpecBuilder.addStatement(format, objAargs);
							}
						}

					}

				}

				if(test.getExecution()!=null)
				{
					List<Map<String, List<Object>>> executionStatements = test.getExecution().getStatements();
					if(executionStatements!=null && executionStatements.size()>0)
					{
						methodSpecBuilder.addCode("\n");
						for(Map<String, List<Object>> expMap:executionStatements)
						{
							for(Map.Entry<String, List<Object>> executionEntry:expMap.entrySet())
							{
								String format = executionEntry.getKey();

								Object[] objAargs = new Object [executionEntry.getValue().size()];
								executionEntry.getValue().toArray(objAargs);
								methodSpecBuilder.addStatement(format, objAargs);
							}
						}

					}

				}

				if(test.getVerification()!=null)
				{
					List<Map<String, List<Object>>> verificationStatements = test.getVerification().getStatements();
					if(verificationStatements!=null && verificationStatements.size()>0)
					{
						methodSpecBuilder.addCode("\n");
						for(Map<String, List<Object>> verificationMap:verificationStatements)
						{
							for(Map.Entry<String, List<Object>> verificationEntry:verificationMap.entrySet())
							{
								String format = verificationEntry.getKey();
								Object[] objAargs = null;
								if(verificationEntry.getValue()!=null && verificationEntry.getValue().size()>0)
								{
									objAargs = new Object [verificationEntry.getValue().size()];
									verificationEntry.getValue().toArray(objAargs);
								}

								if(objAargs!=null)
								{
									methodSpecBuilder.addStatement(format, objAargs);
								}
								else
								{
									methodSpecBuilder.addStatement(format);
								}
							}
						}

					}

				}

				MethodSpec methodSpec =	methodSpecBuilder.build();
				testMethods.add(methodSpec);

			}
			
		}









		/*
    	for (Statement statme : methodDeclaration.getBody().getStmts()) {

			if(statme instanceof IfStmt)
			{
				IfStmt ifStatement = (IfStmt) statme;
				Expression expression = ifStatement.getCondition();
				Statement state = ifStatement.getThenStmt();
				System.out.println();

			}

			if (statme instanceof ForeachStmt) {
				ForeachStmt foreachStmt = (ForeachStmt) statme;
				System.out.println(foreachStmt.getIterable());
			}
		}


	       AnnotationSpec annotationSpec = AnnotationSpec.builder(RunWith.class).build();

	       ClassName cls = ClassName.get(cu.getPackage().getName().toString(), className);  

	        //FieldSpec field = FieldSpec.builder(cls, name, modifiers)


	        TypeSpec helloWorld = TypeSpec.classBuilder(className+"Test")
	        	    .addModifiers(Modifier.PUBLIC)
	        	    .addMethod(methodSpec)
	        	    .addAnnotation(AnnotationSpec.builder(RunWith.class).addMember("value","$T.$L",PowerMockRunner.class,"class").build())
	        	    .addAnnotation(AnnotationSpec.builder(PrepareForTest.class).addMember("value","$T.$L", Collections.class,"class")
	        	    		.addMember("value","$T.$L",Arrays.class,"class").build())
	        	    .build();

	        JavaFile javaFile = JavaFile.builder(cu.getPackage().getName().toString(), helloWorld)
	        		.addStaticImport(Assert.class, "assertEquals")
	        	    .build();

	        javaFile.writeTo(new File("E:/Workarea/JavaParser/Files/"));*/

	}

	private static void doSomething(FieldDeclaration fieldDeclartion) {
		List<VariableDeclarator> fieldDeclarationExprs = fieldDeclartion
				.getVariables();
		for (VariableDeclarator variable : fieldDeclarationExprs) {
			System.out.println(variable.getInit());
		}

	}


	private boolean isMultipleTestScenarios(List<Statement> statements, Method method)
	{
		if(statements!=null && statements.size()>0)
		{
			for(Statement statement:statements)
			{
				if(statement instanceof IfStmt)
				{
					return true;
				}
			}

		}

		return false;

	}


	private TestCase processStatements(List<Statement> statements,Method method)
	{

		TestCase testCase = null;
		if(statements!=null && statements.size()>0)
		{
			testCase = new TestCase();
			testCase.setTestCaseName("test_"+method.getName());


			for(Statement statement:statements)
			{


				if(statement instanceof IfStmt)
				{
					processIfStatement((IfStmt) statement,method,testCase);
				}
				else if(statement instanceof ExpressionStmt)
				{
					processExpressionStatement((ExpressionStmt) statement,method,testCase);
				}
				else if(statement instanceof ReturnStmt)
				{
					processReturnStatement((ReturnStmt) statement,method,testCase);
				}
				else if(statement instanceof ForeachStmt)
				{
					processForEachStatement((ForeachStmt) statement);
				}
				else if(statement instanceof ForStmt)
				{
					processForStatement((ForStmt) statement);
				}
				else if(statement instanceof TypeDeclarationStmt)
				{
					processTypeDeclartionStatement((TypeDeclarationStmt) statement);
				}
				else if(statement instanceof ExplicitConstructorInvocationStmt)
				{
					processExplicitConstructorInvocationStatement((ExplicitConstructorInvocationStmt) statement);
				}
				else if(statement instanceof DoStmt)
				{
					processDoStatement((DoStmt) statement);
				}
				else if(statement instanceof WhileStmt)
				{
					processWhileStatement((WhileStmt) statement);
				}
				else if(statement instanceof TryStmt)
				{
					processTryStatement((TryStmt) statement);
				}
				else if(statement instanceof ThrowStmt)
				{
					processThrowStatement((ThrowStmt) statement);
				}
				else if(statement instanceof SwitchEntryStmt)
				{
					processSwitchEntryStatement((SwitchEntryStmt) statement);
				}
				else if(statement instanceof SwitchStmt)
				{
					processSwitchStatement((SwitchStmt) statement);
				}


			}


		}

		return testCase;

	}

	private void processIfStatement(IfStmt ifStatement,Method method,TestCase testCase)
	{

		Expression expression = ifStatement.getCondition();
		Statement thenStatement = ifStatement.getThenStmt();
		Statement elseStatement = ifStatement.getElseStmt();

		processExpression(expression,method,testCase);




	}


	private void processBlockStatement(BlockStmt blcokStatement)
	{

	}

	private void processExpressionStatement(ExpressionStmt expressionStatement,Method method,TestCase testCase)
	{
		Expression expression = expressionStatement.getExpression();

		processExpression(expression,method,testCase);



	}

	private void processReturnStatement(ReturnStmt returnStatement,Method method,TestCase testCase)
	{
		Expression expression = returnStatement.getExpr();
		processExpression(expression,method,testCase);
	}

	private void processForEachStatement(ForeachStmt forEachStatement)
	{
		Expression expression = forEachStatement.getIterable();
	}

	private void processForStatement(ForStmt forStatement)
	{

	}

	private void processStatement(Statement statement)
	{

	}

	private void processTypeDeclartionStatement(TypeDeclarationStmt typeDeclartionStatement)
	{

	}

	private void processCatchClause(CatchClause catchClause)
	{

	}

	private void processExplicitConstructorInvocationStatement(ExplicitConstructorInvocationStmt explicitConstructorInvocationStmt)
	{

	}

	private void processDoStatement(DoStmt doStmt)
	{

	}

	private void processWhileStatement(WhileStmt whileStmt)
	{

	}

	private void processTryStatement(TryStmt tryStatement)
	{

	}

	private void processThrowStatement(ThrowStmt throwStmt)
	{

	}

	private void processSwitchEntryStatement(SwitchEntryStmt switchEntryStmt)
	{

	}

	private void processSwitchStatement(SwitchStmt switchStmt)
	{

	}

	private void processExpression(Expression expression,Method method,TestCase testCase)
	{
		if(expression instanceof AnnotationExpr)
		{
			processAnnotationExpression((AnnotationExpr) expression);
		}
		else if(expression instanceof ArrayAccessExpr)
		{
			processArrayAccessExpression((ArrayAccessExpr) expression);
		}
		else if(expression instanceof ArrayCreationExpr)
		{
			processArrayCreationExpression((ArrayCreationExpr) expression);
		}
		else if(expression instanceof ArrayInitializerExpr)
		{
			processArrayIntializerExpression((ArrayInitializerExpr) expression);
		}
		else if(expression instanceof AssignExpr)
		{
			processAssignExpression((AssignExpr) expression);
		}
		else if(expression instanceof BinaryExpr)
		{
			processBinaryExpression((BinaryExpr) expression);
		}
		else if(expression instanceof CastExpr)
		{
			processCastExpression((CastExpr) expression);
		}
		else if(expression instanceof ClassExpr)
		{
			processClassExpression((ClassExpr) expression);
		}
		else if(expression instanceof ConditionalExpr)
		{
			processConditonalExpression((ConditionalExpr) expression);
		}
		else if(expression instanceof EnclosedExpr)
		{
			processEnclosedExpression((EnclosedExpr) expression);
		}
		else if(expression instanceof FieldAccessExpr)
		{
			processFieldAccessExpression((FieldAccessExpr) expression);
		}
		else if(expression instanceof InstanceOfExpr)
		{
			processInstanceOfExpression((InstanceOfExpr) expression);
		}
		else if(expression instanceof LiteralExpr)
		{
			processLiteralExpression((LiteralExpr) expression);
		}
		else if(expression instanceof MethodCallExpr)
		{
			processMethodCallExpression((MethodCallExpr) expression);
		}
		else if(expression instanceof MethodReferenceExpr)
		{
			processMethodReferenceExpression((MethodReferenceExpr) expression);
		}
		else if(expression instanceof NameExpr)
		{
			processNameExpr((NameExpr) expression);
		}
		else if(expression instanceof ObjectCreationExpr)
		{
			processObjectCreationExpression((ObjectCreationExpr) expression);
		}
		else if(expression instanceof SuperExpr)
		{
			processSuperExpression((SuperExpr) expression);
		}
		else if(expression instanceof ThisExpr)
		{
			processThisExpression((ThisExpr) expression);
		}
		else if(expression instanceof UnaryExpr)
		{
			processUnaryExpression((UnaryExpr) expression);
		}
		else if(expression instanceof VariableDeclarationExpr)
		{
			processVariableDeclarationExpression((VariableDeclarationExpr) expression,method,testCase);
		}

	}

	private void processAnnotationExpression(AnnotationExpr annotationExpr)
	{

	}

	private void processArrayAccessExpression(ArrayAccessExpr arrayAccessExpr)
	{

	}

	private void processArrayCreationExpression(ArrayCreationExpr arrayCreationExpr)
	{

	}

	private void processArrayIntializerExpression(ArrayInitializerExpr arrayInitializerExpr)
	{

	}

	private void processAssignExpression(AssignExpr assignExpr)
	{

	}

	private void processBinaryExpression(BinaryExpr binaryExpr)
	{
		Expression leftExpression = binaryExpr.getLeft();
		Expression rightExpression = binaryExpr.getRight();
		BinaryExpr.Operator opeartor = binaryExpr.getOperator();

		TestCase testCaseOne = new TestCase();
		if(opeartor.name().equalsIgnoreCase("equals"))
		{
			testCaseOne.setTestCaseName("test_"+leftExpression+"_equals_"+rightExpression.toString());
		}

		testCases.add(testCaseOne);

		TestCase testCaseTwo = new TestCase();
		if(opeartor.name().equalsIgnoreCase("equals"))
		{
			testCaseTwo.setTestCaseName("test_"+leftExpression+"_Notequals_"+rightExpression.toString());
		}

		testCases.add(testCaseTwo);


	}

	private void processCastExpression(CastExpr castExpr)
	{

	}

	private void processClassExpression(ClassExpr classExpr)
	{

	}

	private void processConditonalExpression(ConditionalExpr conditionalExpr)
	{
		Expression conditionExpr = conditionalExpr.getCondition();
		Expression getThExpression = conditionalExpr.getThenExpr();
		Expression getElseExpression = conditionalExpr.getElseExpr();
	}

	private void processEnclosedExpression(EnclosedExpr enclosedExpr)
	{

	}

	private void processFieldAccessExpression(FieldAccessExpr fieldAccessExpr)
	{

	}

	private void processInstanceOfExpression(InstanceOfExpr instanceOfExpr)
	{

	}

	private void processLiteralExpression(LiteralExpr literalExpr)
	{

	}

	private void processMethodCallExpression(MethodCallExpr methodCallExpr)
	{
		List<Expression> args = methodCallExpr.getArgs();
		String name = methodCallExpr.getName();
		Expression expression = methodCallExpr.getScope();
		List<Type> types = methodCallExpr.getTypeArgs();
	}

	private void processMethodReferenceExpression(MethodReferenceExpr methodReferenceExpr)
	{

	}

	private void processNameExpr(NameExpr nameExpr)
	{

	}

	private void processObjectCreationExpression(ObjectCreationExpr objectCreationExpr)
	{

	}

	private void processSuperExpression(SuperExpr superExpr)
	{

	}

	private void processThisExpression(ThisExpr thisExpr)
	{

	}

	private void processTypeExpression(TypeExpr typeExpr)
	{

	}

	private void processUnaryExpression(UnaryExpr unaryExpression)
	{

	}

	private void processVariableDeclarationExpression(VariableDeclarationExpr variableDeclarationExpr,Method method,TestCase testCase)
	{
		Expectation  expectaion = null;
		List<Map<String, List<Object>>> statements = new ArrayList<Map<String,List<Object>>>();
		if(testCase.getExpectation()!=null )
		{
			expectaion = testCase.getExpectation();

			if(expectaion.getStatements()==null)
			{

				expectaion.setStatements(statements);
			}
		}
		else
		{
			expectaion = new Expectation();
			expectaion.setStatements(statements);
		}

		int modifiers = variableDeclarationExpr.getModifiers();
		Type type = variableDeclarationExpr.getType();
		List<VariableDeclarator> variableDeclartors = variableDeclarationExpr.getVars();

		Map<String,List<Object>> statementMap = new HashMap<String, List<Object>>();

		for(VariableDeclarator variable:variableDeclartors)
		{
			System.out.println("ID:"+variable.getId());
			System.out.println("Expression:"+variable.getInit());
			Expression expression = variable.getInit();
			if(expression instanceof MethodCallExpr)
			{
				MethodCallExpr expr = (MethodCallExpr) expression;

				if(isPrivateMethod(expr.getName()))
				{

				}
				else
				{
					String code = null;
					if(isArray(type.toString()))
					{
						code = "$T[] "+variable.getId().toString()+" = new $T[]";
						String typeString = type.toString();
						typeString =typeString.replace("[]", "");
						typeString =typeString.replace(" ", "");

						String reslovedImport = resolveImportsForType(typeString,imports,packageDeclartion,className);
						List<Object> objectList = new ArrayList<Object>();

						if(reslovedImport.contains("."))
						{
							String className = reslovedImport.substring(reslovedImport.lastIndexOf('.')+1,reslovedImport.length()); 
							String packageName  = reslovedImport.substring(0,reslovedImport.lastIndexOf('.'));
							ClassName mock= ClassName.get(packageName, className);
							objectList.add(mock);
							objectList.add(mock);

						}
						else
						{
							objectList.add(type.toString());
						}

						statementMap.put(code, objectList);
						
//						Class cl = Arrays.class;
//						PowerMockito.stub(PowerMockito.method(cl, "methodName",String.class)).toReturn("");

						if(expr.getArgs()!=null && expr.getArgs().size()>0 )
						{
							code = "$T.stub($T.method("+className+".class,"+expr.getName();
							int count = 0;
							for(Expression exp:expr.getArgs())
							{
								if(count!=expr.getArgs().size())
								{
									code = code+",";
								}

								code = code+resolveArgs(exp.toString(), method)+".class";

								count++;
							}

							code = code+")).toReturn("+variable.getId()+")";


						}
						else
						{
							code = "$T.stub($T.method("+className+".class,"+expr.getName()+")).toReturn("+variable.getId()+")";
						}
						
						List<Object> obList = new ArrayList<Object>();
						obList.add(PowerMockito.class);
						obList.add(PowerMockito.class);
						statementMap.put(code, obList);
						
						statements.add(statementMap);
						
					}

				}

			}
		}
		
		testCase.setExpectation(expectaion);
	}

	private static void processConstructor(
			ConstructorDeclaration constructorDeclaration) {
		System.out.println(constructorDeclaration);
	}

	private String resolveImportsForType(String type,List<ImportDeclaration> importDeclartions,PackageDeclaration belongPackage,String className)
	{
		String importStatement = null;
		String typeDeclared = type.toString();
		String importParser = null;
		String classNameDeclared = null;
		String packageDeclared = null;
		int startIndex;
		int endIndex;
		if(typeDeclared != null )
		{
			typeDeclared = typeDeclared.trim();
			if(typeDeclared.equals(className))
			{
				importStatement = belongPackage.toString()+"."+className;
			}
			else{
				if(importDeclartions != null && importDeclartions.size()>0)
				{
					for(ImportDeclaration imports:importDeclartions)
					{
						importParser = imports.toString();

						importParser = importParser.split(" ")[1];
						importParser = importParser.replace(";", "");
						System.out.println("ImportParser:"+importParser);
						startIndex = importParser.lastIndexOf('.')+1;
						endIndex = importParser.length();
						packageDeclared = importParser.substring(0,startIndex);
						classNameDeclared = importParser.substring(startIndex, endIndex);
						classNameDeclared = classNameDeclared.trim();
						if(typeDeclared.equals(classNameDeclared))
						{

							importStatement = packageDeclared+classNameDeclared;
							break;
						}	
					}
				}
			}
		}

		if(importStatement == null)
		{
			importStatement = typeDeclared;
		}

		return importStatement;
	}

	public List<ImportDeclaration> getImports() {
		return imports;
	}

	public void setImports(List<ImportDeclaration> imports) {
		this.imports = imports;
	}

	public List<TypeDeclaration> getTypes() {
		return types;
	}

	public void setTypes(List<TypeDeclaration> types) {
		this.types = types;
	}


	private List<TestCase> prepareTestCase()
	{
		List<TestCase> testCases = new ArrayList<TestCase>();

		return testCases;
	}

	public List<MethodSpec> getMethods() {
		return methods;
	}

	public void setMethods(List<MethodSpec> methods) {
		this.methods = methods;
	}


	private boolean isPrivateMethod(String methodName)
	{
		boolean isPrivate = false;

		if(methodName!=null)
		{
			methodName = methodName.trim();
		}


		for(MethodDeclaration methodDeclaration:methodDeclarations)
		{
			if(methodName.equals(methodDeclaration.getName().toString()))
			{
				int modifier = methodDeclaration.getModifiers();

				if(modifier == 2)
				{
					isPrivate = true;
					break;
				}
			}
		}

		return isPrivate;
	}

	private boolean isArray(String expression)
	{
		if(expression.contains("[]"))
		{
			return true;
		}
		return false;
	}

	private String resolveArgs(String name,Method method)
	{
		String argClass = null;
		String key = null;
		String value = null;
		if(method.getParamMap()!=null)
		{
			for(Map.Entry<String, String> entry:method.getParamMap().entrySet() )
			{
				key = entry.getKey();
				value = entry.getValue();
				if(name.equals(value))
				{
					if(key.contains("."))
					{
						argClass = key.substring(key.lastIndexOf(".")+1,key.length());
					}
					else
					{
						argClass = key;
					}
					break;
				}
			}
		}




		return argClass;
	}

}
