package cn.edu.nudt.ast;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.*;

public class AstGeneratror {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String FilePath = "/Users/**/Documents/test/data/SqrtAlgorithm.java";
		CompilationUnit result = getCompilationUnit(FilePath);
		TreeNode root = getASTNodes(result);
		System.out.println("----------------------Printing Result----------------------");
		printASTNodes(root);
		System.out.println("---------------------------Done!---------------------------");
	}
	
	public static CompilationUnit getCompilationUnit(String javaFilePath) {
		
		byte[] input = null;
		try {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(javaFilePath));
			input = new byte[bufferedInputStream.available()];
			bufferedInputStream.read(input);
			bufferedInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ASTParser astParser = ASTParser.newParser(AST.JLS8);
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
		astParser.setSource(new String(input).toCharArray());
		CompilationUnit compilationUnit = (CompilationUnit) astParser.createAST(null);
		//System.out.println(compilationUnit.toString());
		return compilationUnit;
	}
	
	public static TreeNode getASTNodes(CompilationUnit result) {
		
		//Root Node
		TreeNode Root = new TreeNode();
		Root.data = "CompilationUnit";
		
		//Package Node
		TreeNode Package = new TreeNode();
		Package.data = "PackageDeclaration: " + result.getPackage().getName();
		Package.parent = Root;
		Root.childlist.add(Package);
		
		//Import Node
		TreeNode Import = new TreeNode();
		Import.data = "Import: ";
		Import.parent = Root;
		Root.childlist.add(Import);
		
		//show import declarations in order
		List importList = result.imports();
		for (Object obj : importList) {
			TreeNode importNode = new TreeNode();
			ImportDeclaration importDec = (ImportDeclaration) obj;
			importNode.data = importDec.getName().toString();
			importNode.parent = Import;
			Import.childlist.add(importNode);
		}

		//Class Node
		List types = result.types();
		TypeDeclaration typeDec = (TypeDeclaration) types.get(0);
		TreeNode Class = new TreeNode();
		Class.data = "TypeDeclaration: " + typeDec.getName();
		Class.parent = Root;
		Root.childlist.add(Class);
		
		TreeNode Body = new TreeNode();
		Body.data = "BodyDeclaration";
		Body.parent = Class;
		Class.childlist.add(Body);
		
		//Field Node
		FieldDeclaration fieldDec[] = typeDec.getFields();
		for (FieldDeclaration field : fieldDec) {
			TreeNode Field = new TreeNode();
			Field.data = "Field Node: ";
			Field.parent = Body;
			Body.childlist.add(Field);
			
			TreeNode fieldType = new TreeNode();
			fieldType.data = "Field type:" + field.getType().toString();
			fieldType.parent = Field;
			Field.childlist.add(fieldType);
			
			TreeNode fieldFrag = new TreeNode();
			fieldFrag.data = "Field fragment:" + field.fragments().get(0);
			fieldFrag.parent = Field;
			Field.childlist.add(fieldFrag);
		}

		//Method Node
		MethodDeclaration methodDec[] = typeDec.getMethods();
		for (MethodDeclaration method : methodDec) {
			TreeNode Method = new TreeNode();
			Method.data = "Method Node: ";
			Method.parent = Body;
			Body.childlist.add(Method);
		
			//get method return type
			TreeNode MethodReturn = new TreeNode();
			MethodReturn.data = "Return Type: " + method.getReturnType2().toString();
			MethodReturn.parent = Method;
			Method.childlist.add(MethodReturn);
			
			//get method name
			TreeNode MethodName = new TreeNode();
			MethodName.data = "Method Name: " + method.getName().toString();
			MethodName.parent = Method;
			Method.childlist.add(MethodName);

			//get method parameters
			TreeNode MethodPara = new TreeNode();
			MethodPara.data = "Method Parameters: " + method.parameters();
			MethodPara.parent = Method;
			Method.childlist.add(MethodPara);
			
			//get method body
			TreeNode MethodBody = new TreeNode();
			MethodBody.data = "Method Body: ";
			MethodBody.parent = Method;
			Method.childlist.add(MethodBody);
			Block body = method.getBody();
			List statements = body.statements();   //get the statements of the method body
			Iterator iter = statements.iterator();
			while (iter.hasNext()) {
				//get each statement
				Statement stmt = (Statement) iter.next();
				if (stmt instanceof ExpressionStatement) {
					ExpressionStatement expressStmt = (ExpressionStatement) stmt;
					Expression express = expressStmt.getExpression();
					if (express instanceof Assignment) {
						TreeNode AssignmentNode = new TreeNode();
						AssignmentNode.data = "Assignment: ";
						AssignmentNode.parent = MethodBody;
						MethodBody.childlist.add(AssignmentNode);
						
						Assignment assign = (Assignment) express;
						TreeNode AssignmentLHS = new TreeNode();
						AssignmentLHS.data = "LHS:" + assign.getLeftHandSide();
						AssignmentLHS.parent = AssignmentNode;
						AssignmentNode.childlist.add(AssignmentLHS);
						
						TreeNode AssignmentOP = new TreeNode();
						AssignmentOP.data = "Op:" + assign.getOperator();
						AssignmentOP.parent = AssignmentNode;
						AssignmentNode.childlist.add(AssignmentOP);
						
						TreeNode AssignmentRHS = new TreeNode();
						AssignmentRHS.data = "RHS:" + assign.getRightHandSide();
						AssignmentRHS.parent = AssignmentNode;
						AssignmentNode.childlist.add(AssignmentRHS);

					} else if (express instanceof MethodInvocation) {
						TreeNode MethodInvocationNode = new TreeNode();
						MethodInvocationNode.data = "Method Invovation: ";
						MethodInvocationNode.parent = MethodBody;
						MethodBody.childlist.add(MethodInvocationNode);
						MethodInvocation mi = (MethodInvocation) express;
						
						TreeNode MethodInvocationName = new TreeNode();
						MethodInvocationName.data = "invocation name:" + mi.getName();
						MethodInvocationName.parent = MethodInvocationNode;
						MethodInvocationNode.childlist.add(MethodInvocationName);
						
						TreeNode MethodInvocationExp = new TreeNode();
						MethodInvocationExp.data = "invocation exp:" + mi.getExpression();
						MethodInvocationExp.parent = MethodInvocationNode;
						MethodInvocationNode.childlist.add(MethodInvocationExp);
						
						TreeNode MethodInvocationArg = new TreeNode();
						MethodInvocationArg.data = "invocation arg:" + mi.arguments();
						MethodInvocationArg.parent = MethodInvocationNode;
						MethodInvocationNode.childlist.add(MethodInvocationArg);	
					}
				} else if(stmt instanceof VariableDeclarationStatement) {
					VariableDeclarationStatement variableDecStatement = (VariableDeclarationStatement) stmt;
					TreeNode VariableDecNode = new TreeNode();
					VariableDecNode.data = "VariableDeclarationStatement: ";
					VariableDecNode.parent = MethodBody;
					MethodBody.childlist.add(VariableDecNode);
					
					TreeNode VariableTypeNode = new TreeNode();
					VariableTypeNode.data = "Variable Type: " + variableDecStatement.getType();
					VariableTypeNode.parent = VariableDecNode;
					VariableDecNode.childlist.add(VariableTypeNode);
					
					TreeNode VariableFragNode = new TreeNode();
					VariableFragNode.data = "Variable Fragments: ";
					VariableFragNode.parent = VariableDecNode;
					VariableDecNode.childlist.add(VariableFragNode);
					
					for (int i = 0; i< variableDecStatement.fragments().size(); i++) {
						TreeNode VariableFrag = new TreeNode();
						VariableFrag.data = variableDecStatement.fragments().get(i).toString();
						VariableFrag.parent = VariableFragNode;
						VariableFragNode.childlist.add(VariableFrag);
					}
				} else if(stmt instanceof ReturnStatement) {
					ReturnStatement returnStatement = (ReturnStatement) stmt;
					TreeNode ReturnNode = new TreeNode();
					ReturnNode.data = "ReturnStatement: " + returnStatement.getExpression();
					ReturnNode.parent = MethodBody;
					MethodBody.childlist.add(ReturnNode);
				}else if (stmt instanceof AssertStatement) {
					AssertStatement assertStatement = (AssertStatement) stmt;
					TreeNode AssertNode = new TreeNode();
					AssertNode.data = "AssertStatement:";
					AssertNode.parent = MethodBody;
					MethodBody.childlist.add(AssertNode);
					
					TreeNode AssertExpNode = new TreeNode();
					AssertExpNode.data = "AssertExpression: " + assertStatement.getExpression();
					AssertExpNode.parent = AssertNode;
					AssertNode.childlist.add(AssertExpNode);
					
					TreeNode AssertMesNode = new TreeNode();
					AssertMesNode.data = "AssertMessage: " + assertStatement.getMessage();
					AssertMesNode.parent = AssertNode;
					AssertNode.childlist.add(AssertMesNode);
				} else if (stmt instanceof IfStatement) {
					IfStatement ifStatement = (IfStatement) stmt;
					TreeNode IfNode = new TreeNode();
					IfNode.data = "IfStatement:";
					IfNode.parent = MethodBody;
					MethodBody.childlist.add(IfNode);
					
					TreeNode IfExpNode = new TreeNode();
					IfExpNode.data = "IfExpression:" + ifStatement.getExpression();
					IfExpNode.parent = IfNode;
					IfNode.childlist.add(IfExpNode);
					
					TreeNode IfThenNode = new TreeNode();
					IfThenNode.data = "IfStatement:" + ifStatement.getThenStatement();
					IfThenNode.parent = IfNode;
					IfNode.childlist.add(IfThenNode);
					
					TreeNode IfElseNode = new TreeNode();
					IfElseNode.data = "ElseStatement:" + ifStatement.getElseStatement();
					IfElseNode.parent = IfNode;
					IfNode.childlist.add(IfElseNode);
				} else if(stmt instanceof DoStatement) {
					DoStatement doStatement = (DoStatement) stmt;
					TreeNode DoNode = new TreeNode();
					DoNode.data = "DoStatement:";
					DoNode.parent = MethodBody;
					MethodBody.childlist.add(DoNode);

					TreeNode DoExpNode = new TreeNode();
					DoExpNode.data = "DoExpression:" + doStatement.getExpression();
					DoExpNode.parent = DoNode;
					DoNode.childlist.add(DoExpNode);
					
					TreeNode DoBodyNode = new TreeNode();
					DoBodyNode.data = "DoBody:" + doStatement.getBody();
					DoBodyNode.parent = DoNode;
					DoNode.childlist.add(DoBodyNode);
				} else if(stmt instanceof WhileStatement) {
					WhileStatement whileStatement = (WhileStatement) stmt;
					TreeNode WhileNode = new TreeNode();
					WhileNode.data = "WhileStatement:";
					WhileNode.parent = MethodBody;
					MethodBody.childlist.add(WhileNode);

					TreeNode WhileExpNode = new TreeNode();
					WhileExpNode.data = "WhileExpression:" + whileStatement.getExpression();
					WhileExpNode.parent = WhileNode;
					WhileNode.childlist.add(WhileExpNode);
					
					TreeNode WhileBodyNode = new TreeNode();
					WhileBodyNode.data = "WhileBody:" + whileStatement.getBody();
					WhileBodyNode.parent = WhileNode;
					WhileNode.childlist.add(WhileBodyNode);
				} else if(stmt instanceof ForStatement) {
					ForStatement forStatement = (ForStatement) stmt;
					TreeNode ForNode = new TreeNode();
					ForNode.data = "ForStatement:";
					ForNode.parent = MethodBody;
					MethodBody.childlist.add(ForNode);
					
					for(int i = 0; i< forStatement.initializers().size(); i++) {
						TreeNode ForInitNode = new TreeNode();
						ForInitNode.data = "ForInit:" + forStatement.initializers().get(i);
						ForInitNode.parent = ForNode;
						ForNode.childlist.add(ForInitNode);
					}
					
					TreeNode ForExpNode = new TreeNode();
					ForExpNode.data = "ForExpression:" + forStatement.getExpression();
					ForExpNode.parent = ForNode;
					ForNode.childlist.add(ForExpNode);

					for(int i = 0; i< forStatement.updaters().size(); i++) {
						TreeNode ForUpdatersNode = new TreeNode();
						ForUpdatersNode.data = "ForUpdaters:" + forStatement.updaters().get(i);
						ForUpdatersNode.parent = ForNode;
						ForNode.childlist.add(ForUpdatersNode);
					}
					
					TreeNode ForBodyNode = new TreeNode();
					ForBodyNode.data = "ForStatement:" + forStatement.getBody();
					ForBodyNode.parent = ForNode;
					ForNode.childlist.add(ForBodyNode);
				} else if(stmt instanceof SwitchStatement) {
					SwitchStatement switchStatement = (SwitchStatement) stmt;
					TreeNode SwitchNode = new TreeNode();
					SwitchNode.data = "SwitchStatement:";
					SwitchNode.parent = MethodBody;
					MethodBody.childlist.add(SwitchNode);
					
					TreeNode SwitchExpNode = new TreeNode();
					SwitchExpNode.data = "SwitchExpression:" + switchStatement.getExpression();
					SwitchExpNode.parent = SwitchNode;
					SwitchNode.childlist.add(SwitchExpNode);
					
					for(Object Expression : switchStatement.statements()) {
						if(Expression instanceof SwitchCase) {
							SwitchCase switchCase = (SwitchCase) Expression;
							TreeNode SwitchCaseNode = new TreeNode();
							if(!switchCase.isDefault())
								SwitchCaseNode.data = "Case:" + switchCase.getExpression();		
							else
								SwitchCaseNode.data = "Default";
							SwitchCaseNode.parent = SwitchNode;
							SwitchNode.childlist.add(SwitchCaseNode);
						} else {
							TreeNode SwitchCaseNode = new TreeNode();
							SwitchCaseNode.data = Expression.toString();
							SwitchCaseNode.parent = SwitchNode;
							SwitchNode.childlist.add(SwitchCaseNode);
						}
					}
					
				}
			}
		}		
		return Root;
	}

	public static void printASTNodes(TreeNode treenode) {
		System.out.println(treenode.data);
		if(treenode.childlist.size()!=0) {
			for(TreeNode node : treenode.childlist)
				printASTNodes(node);
		}
	}
	
}
