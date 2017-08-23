package org.mybatis.generator.codegen.mybatis3.model;

import static org.mybatis.generator.internal.util.JavaBeansUtil.getGetterMethodName;
import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;




public class BaseExampleGenerator  extends AbstractJavaGenerator {

	@Override
	public List<CompilationUnit> getCompilationUnits() {
		progressCallback.startTask(getString("Progress.6", "baseExample"));
		CommentGenerator commentGenerator = context.getCommentGenerator();

		FullyQualifiedJavaType type = new FullyQualifiedJavaType(context.getBaseExampleName());
		TopLevelClass topLevelClass = new TopLevelClass(type);
		topLevelClass.setVisibility(JavaVisibility.PUBLIC);
		topLevelClass.setAbstract(true);
		commentGenerator.addJavaFileComment(topLevelClass);

		// add default constructor
		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setConstructor(true);
		method.setName(type.getShortName());
		method.addBodyLine("oredCriteria = new ArrayList<GeneratedCriteria>();");
		method.addBodyLine("columnContainerMap = new HashMap<String,ColumnContainerBase>();");
		method.addBodyLine("leftJoinTableSet = new HashSet<String>();");// add by suman
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);

		// add field, getter, setter for orderby clause
		Field field = new Field();
		field.setVisibility(JavaVisibility.PROTECTED);
		field.setType(FullyQualifiedJavaType.getStringInstance());
		field.setName("orderByClause");
		commentGenerator.addFieldComment(field, introspectedTable);
		topLevelClass.addField(field);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("setOrderByClause");
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "orderByClause"));
		method.addBodyLine("this.orderByClause = orderByClause;");
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(FullyQualifiedJavaType.getStringInstance());
		method.setName("getOrderByClause");
		method.addBodyLine("return orderByClause;");
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);

		// add field, getter, setter for distinct
		field = new Field();
		field.setVisibility(JavaVisibility.PROTECTED);
		field.setType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
		field.setName("distinct");
		commentGenerator.addFieldComment(field, introspectedTable);
		topLevelClass.addField(field);
		
		// add by suman start
		field = new Field();
		field.setVisibility(JavaVisibility.PROTECTED);
		FullyQualifiedJavaType stringOftableName = FullyQualifiedJavaType.getStringInstance();
		field.setType(stringOftableName);
		field.setName("tableName");
		topLevelClass.addField(field);
		
		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(field.getType());
		method.setName(getGetterMethodName(field.getName(), field.getType()));
		method.addBodyLine("return tableName;");
		topLevelClass.addMethod(method);
		
		
		// add by suman end

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("setDistinct");
		method.addParameter(new Parameter(FullyQualifiedJavaType.getBooleanPrimitiveInstance(), "distinct"));
		method.addBodyLine("this.distinct = distinct;");
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
		method.setName("isDistinct");
		method.addBodyLine("return distinct;");
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);

		// add field and methods for the list of ored criteria
		field = new Field();
		field.setVisibility(JavaVisibility.PROTECTED);

		FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("java.util.List<GeneratedCriteria>");
		field.setType(fqjt);
		field.setName("oredCriteria");
		commentGenerator.addFieldComment(field, introspectedTable);
		topLevelClass.addField(field);
		
		

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(fqjt);
		method.setName("getOredCriteria");
		method.addBodyLine("return oredCriteria;");
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);
		
		
		// add by suman start
		field = new Field();
		field.setVisibility(JavaVisibility.PROTECTED);

		fqjt = new FullyQualifiedJavaType("java.util.Map<String,ColumnContainerBase>");
		field.setType(fqjt);
		field.setName("columnContainerMap");
		commentGenerator.addFieldComment(field, introspectedTable);
		topLevelClass.addField(field);
		
		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(new FullyQualifiedJavaType("java.util.Set<ColumnContainerBase>"));
		method.setName("getColumnContainerSet");
		method.addBodyLine("if(columnContainerMap.size()==0){");
		method.addBodyLine("columnContainerMap.put(getTableName(), createColumns());");
		method.addBodyLine("}");
		method.addBodyLine("return new HashSet(columnContainerMap.values());");
		topLevelClass.addMethod(method);
		
		field = new Field();
		field.setVisibility(JavaVisibility.PROTECTED);

		FullyQualifiedJavaType fqjtype = new FullyQualifiedJavaType("java.util.Set<String>");
		field.setType(fqjtype);
		field.setName("leftJoinTableSet");
		commentGenerator.addFieldComment(field, introspectedTable);
		topLevelClass.addField(field);
		
		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(field.getType());
		method.setName(getGetterMethodName(field.getName(), field.getType()));
		method.addBodyLine("return leftJoinTableSet;");
		topLevelClass.addMethod(method);
		
		
		
		// add by suman end
		
		

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("or");
		method.addParameter(new Parameter(FullyQualifiedJavaType.getGeneratedCriteriaInstance(), "criteria"));
		method.addBodyLine("oredCriteria.add(criteria);");
		method.addBodyLine("if(!criteria.getTableName().equals(getTableName())){");
		method.addBodyLine("leftJoinTableSet.add(criteria.getTableName());");
		method.addBodyLine("}");
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);

	/*	method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("or");
		method.setReturnType(FullyQualifiedJavaType.getGeneratedCriteriaInstance());
		method.addBodyLine("GeneratedCriteria criteria = createCriteriaInternal();");
		method.addBodyLine("oredCriteria.add(criteria);");
		method.addBodyLine("return criteria;");
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);
		*/
		
		

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("and");
		method.setReturnType(FullyQualifiedJavaType.getGeneratedCriteriaInstance());
		method.addParameter(new Parameter(FullyQualifiedJavaType.getGeneratedCriteriaInstance(), "criteria"));
		method.addBodyLine("GeneratedCriteria oldCriteria =  criteria;");
		method.addBodyLine("if(oredCriteria.size()<=0){");
			method.addBodyLine("oredCriteria.add(criteria);");
		method.addBodyLine("}else{");
			method.addBodyLine("oldCriteria = oredCriteria.get(oredCriteria.size()-1);");
			method.addBodyLine("oldCriteria.getCriteria().addAll(criteria.getCriteria());");
		method.addBodyLine("}");

    	method.addBodyLine("if(!criteria.getTableName().equals(getTableName())){");
			method.addBodyLine("leftJoinTableSet.add(criteria.getTableName());");
		method.addBodyLine("}");
		method.addBodyLine("return oldCriteria;");
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);
		
		
		
		
	  /* public void addColumnContainer(ColumnContainerBase columnContainer) {
	        columnContainerSet.add(columnContainer);
	        if(!columnContainer.getTableName().equals(getTableName())){
	            leftJoinTableSet.add(columnContainer.getTableName());
	        }
	    }
	*/
		// add by suman start
		/*method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("addColumnContainer");
		method.addParameter(new Parameter(FullyQualifiedJavaType.getColumnContainerBaseInstance(), "columnContainer"));
		
		method.addBodyLine("if(!columnContainerSet.contains(createColumnContainer())){");
		method.addBodyLine(" columnContainerSet.add(createColumnContainer());");
		method.addBodyLine("}");
		method.addBodyLine("columnContainerSet.add(columnContainer);");
		method.addBodyLine("if(!columnContainer.getTableName().equals(getTableName())){");
		method.addBodyLine("leftJoinTableSet.add(columnContainer.getTableName());");
		method.addBodyLine("}");
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);*/
		// add by suman end
		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("createCriteria");
		method.setReturnType(FullyQualifiedJavaType.getGeneratedCriteriaInstance());
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PROTECTED);
		method.setName("createCriteriaInternal");
		method.setReturnType(FullyQualifiedJavaType.getGeneratedCriteriaInstance());
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);
		
		// add by suman start
		method = new Method();
		method.setVisibility(JavaVisibility.PROTECTED);
		method.setName("createColumns");
		method.setReturnType(FullyQualifiedJavaType.getColumnContainerBaseInstance());
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);
		// add by suman end

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("clear");
		method.addBodyLine("oredCriteria.clear();");
		method.addBodyLine("columnContainerMap.clear();");
		method.addBodyLine("leftJoinTableSet.clear();");
		method.addBodyLine("orderByClause = null;");
		method.addBodyLine("distinct = false;");
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);

		// now generate the inner class that holds the AND conditions
		topLevelClass.addInnerClass(getGeneratedCriteriaInnerClass(topLevelClass));

		topLevelClass.addInnerClass(getCriterionInnerClass(topLevelClass));
		
		topLevelClass.addInnerClass(getColumnContainerClass(topLevelClass));
		

		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
		if (context.getPlugins().modelExampleClassGenerated(topLevelClass, introspectedTable)) {
			answer.add(topLevelClass);
		}
		return answer;
	}
	

	private InnerClass getColumnContainerClass(TopLevelClass topLevelClass) {
		Field field;
		Method method;

		InnerClass answer = new InnerClass(FullyQualifiedJavaType.getColumnContainerBaseInstance());

		answer.setVisibility(JavaVisibility.PROTECTED);
		answer.setStatic(true);
		context.getCommentGenerator().addClassComment(answer, introspectedTable);

		method = new Method();
		method.setVisibility(JavaVisibility.PROTECTED);
		method.setName(FullyQualifiedJavaType.getColumnContainerBaseInstance().getShortName());
		method.setConstructor(true);
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "tableName"));
	
	
		method.addBodyLine("super();");
		// add by suman start 
		method.addBodyLine("columnContainerStr = new StringBuffer();");
		method.addBodyLine("this.tableName = tableName;");
		// add by suman end
		answer.addMethod(method);


		// now columnList the isValid method
		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("isValid");
		method.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
		
		method.addBodyLine("return columnContainerStr.length() > 0;");
		answer.addMethod(method);


		//��Ӵ���getAllColumn���������
		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("getAllColumn");
		method.setReturnType(FullyQualifiedJavaType.getStringBufferInstance());
		method.addBodyLine("return columnContainerStr;");
		answer.addMethod(method);


	
		field = new Field();
		field.setVisibility(JavaVisibility.PRIVATE);
		field.setType(FullyQualifiedJavaType.getStringBufferInstance());
		field.setName("columnContainerStr");
		answer.addField(field);
		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(field.getType());
		method.setName(getGetterMethodName(field.getName(), field.getType()));
		method.addBodyLine("return columnContainerStr;");
		answer.addMethod(method);
		
		method = new Method();
		method.setVisibility(JavaVisibility.PROTECTED);
		method.setName("addColumnStr");
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "column"));
		method.addBodyLine("if (columnContainerStr.length() > 0) {");
		method.addBodyLine("columnContainerStr.append(\",\");");
		method.addBodyLine("}");
		method.addBodyLine("columnContainerStr.append(column);");

		answer.addMethod(method);
		// add by suman start
		field = new Field();
		field.setVisibility(JavaVisibility.PRIVATE);
		FullyQualifiedJavaType stringOftableName = FullyQualifiedJavaType.getStringInstance();
		field.setType(stringOftableName);
		field.setName("tableName");
		answer.addField(field);
		
		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(field.getType());
		method.setName(getGetterMethodName(field.getName(), field.getType()));
		method.addBodyLine("return tableName;");
		answer.addMethod(method);
		// add by suman end
		


		return answer;
	}

	private InnerClass getCriterionInnerClass(TopLevelClass topLevelClass) {
		Field field;
		Method method;

		InnerClass answer = new InnerClass(new FullyQualifiedJavaType("Criterion"));
		answer.setVisibility(JavaVisibility.PUBLIC);
		answer.setStatic(true);
		context.getCommentGenerator().addClassComment(answer, introspectedTable);

		field = new Field();
		field.setName("condition");
		field.setType(FullyQualifiedJavaType.getStringInstance());
		field.setVisibility(JavaVisibility.PRIVATE);
		answer.addField(field);
		answer.addMethod(getGetter(field));

		field = new Field();
		field.setName("value");
		field.setType(FullyQualifiedJavaType.getObjectInstance());
		field.setVisibility(JavaVisibility.PRIVATE);
		answer.addField(field);
		answer.addMethod(getGetter(field));

		field = new Field();
		field.setName("secondValue");
		field.setType(FullyQualifiedJavaType.getObjectInstance());
		field.setVisibility(JavaVisibility.PRIVATE);
		answer.addField(field);
		answer.addMethod(getGetter(field));

		field = new Field();
		field.setName("noValue");
		field.setType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
		field.setVisibility(JavaVisibility.PRIVATE);
		answer.addField(field);
		answer.addMethod(getGetter(field));

		field = new Field();
		field.setName("singleValue");
		field.setType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
		field.setVisibility(JavaVisibility.PRIVATE);
		answer.addField(field);
		answer.addMethod(getGetter(field));

		field = new Field();
		field.setName("betweenValue");
		field.setType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
		field.setVisibility(JavaVisibility.PRIVATE);
		answer.addField(field);
		answer.addMethod(getGetter(field));

		field = new Field();
		field.setName("listValue");
		field.setType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
		field.setVisibility(JavaVisibility.PRIVATE);
		answer.addField(field);
		answer.addMethod(getGetter(field));

		field = new Field();
		field.setName("typeHandler");
		field.setType(FullyQualifiedJavaType.getStringInstance());
		field.setVisibility(JavaVisibility.PRIVATE);
		answer.addField(field);
		answer.addMethod(getGetter(field));

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("Criterion");
		method.setConstructor(true);
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition"));
		method.addBodyLine("super();");
		method.addBodyLine("this.condition = condition;");
		method.addBodyLine("this.typeHandler = null;");
		method.addBodyLine("this.noValue = true;");
		answer.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("Criterion");
		method.setConstructor(true);
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition"));
		method.addParameter(new Parameter(FullyQualifiedJavaType.getObjectInstance(), "value"));
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "typeHandler"));
		method.addBodyLine("super();");
		method.addBodyLine("this.condition = condition;");
		method.addBodyLine("this.value = value;");
		method.addBodyLine("this.typeHandler = typeHandler;");
		method.addBodyLine("if (value instanceof List<?>) {");
		method.addBodyLine("this.listValue = true;");
		method.addBodyLine("} else {");
		method.addBodyLine("this.singleValue = true;");
		method.addBodyLine("}");
		answer.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("Criterion");
		method.setConstructor(true);
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition"));
		method.addParameter(new Parameter(FullyQualifiedJavaType.getObjectInstance(), "value"));
		method.addBodyLine("this(condition, value, null);");
		answer.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("Criterion");
		method.setConstructor(true);
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition"));
		method.addParameter(new Parameter(FullyQualifiedJavaType.getObjectInstance(), "value"));
		method.addParameter(new Parameter(FullyQualifiedJavaType.getObjectInstance(), "secondValue"));
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "typeHandler"));
		method.addBodyLine("super();");
		method.addBodyLine("this.condition = condition;");
		method.addBodyLine("this.value = value;");
		method.addBodyLine("this.secondValue = secondValue;");
		method.addBodyLine("this.typeHandler = typeHandler;");
		method.addBodyLine("this.betweenValue = true;");
		answer.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("Criterion");
		method.setConstructor(true);
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "condition"));
		method.addParameter(new Parameter(FullyQualifiedJavaType.getObjectInstance(), "value"));
		method.addParameter(new Parameter(FullyQualifiedJavaType.getObjectInstance(), "secondValue"));
		method.addBodyLine("this(condition, value, secondValue, null);");
		answer.addMethod(method);

		return answer;
	}


	private InnerClass getGeneratedCriteriaInnerClass(TopLevelClass topLevelClass) {
		Field field;
		Method method;

		InnerClass answer = new InnerClass(FullyQualifiedJavaType.getGeneratedCriteriaInstance());

		answer.setVisibility(JavaVisibility.PROTECTED);
		answer.setStatic(true);
		answer.setAbstract(true);
		context.getCommentGenerator().addClassComment(answer, introspectedTable);

		method = new Method();
		method.setVisibility(JavaVisibility.PROTECTED);
		method.setName("GeneratedCriteria");
		method.setConstructor(true);
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "tableName"));
		method.addBodyLine("super();");
		method.addBodyLine("this.criteria = new ArrayList<Criterion>();");
		method.addBodyLine("this.tableName = tableName;");
	
		answer.addMethod(method);

		// now we need to generate the methods that will be used in the SqlMap
		// to generate the dynamic where clause
		topLevelClass.addImportedType(FullyQualifiedJavaType.getNewListInstance());
		topLevelClass.addImportedType(FullyQualifiedJavaType.getNewArrayListInstance());
		
		// add by suman start
		topLevelClass.addImportedType(FullyQualifiedJavaType.getNewSetInstance());
		topLevelClass.addImportedType(FullyQualifiedJavaType.getNewHashSetInstance());
		topLevelClass.addImportedType(FullyQualifiedJavaType.getNewHashMapInstance());
		topLevelClass.addImportedType(FullyQualifiedJavaType.getNewMapInstance());
		// add by suman end
		
		field = new Field();
		field.setVisibility(JavaVisibility.PROTECTED);
		FullyQualifiedJavaType listOfCriterion = new FullyQualifiedJavaType("java.util.List<Criterion>");
		field.setType(listOfCriterion);
		field.setName("criteria");
		answer.addField(field);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(field.getType());
		method.setName(getGetterMethodName(field.getName(), field.getType()));
		method.addBodyLine("return criteria;");
		answer.addMethod(method);
		
		// add by suman start
		field = new Field();
		field.setVisibility(JavaVisibility.PRIVATE);
		FullyQualifiedJavaType stringOftableName = FullyQualifiedJavaType.getStringInstance();
		field.setType(stringOftableName);
		field.setName("tableName");
		answer.addField(field);
		
		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(field.getType());
		method.setName(getGetterMethodName(field.getName(), field.getType()));
		method.addBodyLine("return tableName;");
		answer.addMethod(method);
		// add by suman end
		
		

		return answer;
	}



}
