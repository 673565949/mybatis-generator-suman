/*
 *  Copyright 2012 The MyBatis Team
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.mybatis.generator.codegen.mybatis3.controller;

import static org.mybatis.generator.internal.util.JavaBeansUtil.getJavaBeansField;
import static org.mybatis.generator.internal.util.JavaBeansUtil.getJavaBeansGetter;
import static org.mybatis.generator.internal.util.JavaBeansUtil.getJavaBeansSetter;
import static org.mybatis.generator.internal.util.JavaBeansUtil.getValidPropertyName;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.RootClassInfo;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class ControllerBaseGenerator extends AbstractJavaGenerator {

	public ControllerBaseGenerator() {
		super();
	}

	@Override
	public List<CompilationUnit> getCompilationUnits() {
		FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
		progressCallback.startTask(getString("Progress.8", table.toString())); //$NON-NLS-1$
		Plugin plugins = context.getPlugins();
		CommentGenerator commentGenerator = context.getCommentGenerator();

		FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType().replace(".model", ".controller.base")+"BaseController");
		TopLevelClass topLevelClass = new TopLevelClass(type);
		topLevelClass.setVisibility(JavaVisibility.PUBLIC);
		topLevelClass.setAbstract(true);
		commentGenerator.addJavaFileComment(topLevelClass);

		FullyQualifiedJavaType superClass = getSuperClass();
		FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType().replaceAll("Mapper", "Service").replaceAll(".mapper", ".service.adapter"));
		if (superClass != null) {
			topLevelClass.setSuperClass(superClass);
			
			topLevelClass.addImportedType(superClass);
		}
		FullyQualifiedJavaType mapper = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
		
		topLevelClass.addImportedType( new FullyQualifiedJavaType("javax.annotation.Resource"));
		topLevelClass.addImportedType( new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
		topLevelClass.addImportedType(mapper);
		Field field = new Field();
		field.addAnnotation("@Resource");
		field.setVisibility(JavaVisibility.PRIVATE);
		field.setType(serviceType);
		field.setName(getValidPropertyName(serviceType.getShortName()));
		topLevelClass.addField(field);

		Method method = new Method();
		method.addAnnotation("@Override");
		method.setVisibility(JavaVisibility.PROTECTED);
		method.setConstructor(false);
		method.setName("getExample");
		FullyQualifiedJavaType modelType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
		FullyQualifiedJavaType exampleType = new FullyQualifiedJavaType(introspectedTable.getExampleType());
		Parameter parameter = new Parameter(modelType,modelType.getShortName());
		method.addParameter(parameter);
		FullyQualifiedJavaType baseMapperType = new FullyQualifiedJavaType("com.viontech.base.BaseMapper<"+introspectedTable.getBaseRecordType()+">");
		topLevelClass.addImportedType(baseMapperType);
		topLevelClass.addImportedType(exampleType);
		topLevelClass.addImportedType(serviceType);
		FullyQualifiedJavaType baseExample = new FullyQualifiedJavaType("com.viontech.base.BaseExample");
		topLevelClass.addImportedType(baseExample);
		method.setReturnType(baseExample);
		method.addBodyLine(exampleType.getShortName()+" "+getValidPropertyName(exampleType.getShortName()) +" = new "+exampleType.getShortName()+"();");
		method.addBodyLine("return "+getValidPropertyName(exampleType.getShortName())+";");
		topLevelClass.addMethod(method);
		FullyQualifiedJavaType baseServiceType = new FullyQualifiedJavaType("com.viontech.base.BaseService<"+introspectedTable.getBaseRecordType()+">");
		topLevelClass.addImportedType(baseServiceType);
		method = new Method();
		method.addAnnotation("@Override");
		method.setVisibility(JavaVisibility.PROTECTED);
		method.setConstructor(false);
		method.setName("getService");
		method.setReturnType(baseServiceType);
		method.addBodyLine("return "+getValidPropertyName(serviceType.getShortName())+";");
		topLevelClass.addMethod(method);
		
		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
		if (context.getPlugins().modelBaseRecordClassGenerated(topLevelClass, introspectedTable)) {
			answer.add(topLevelClass);
		}
		return answer;
	}

	private FullyQualifiedJavaType getSuperClass() {
		FullyQualifiedJavaType superClass;
		String rootClass = "com.viontech.base.BaseController<"+introspectedTable.getBaseRecordType()+">";
		if (rootClass != null) {
			superClass = new FullyQualifiedJavaType(rootClass);
		} else {
			superClass = null;
		}

		return superClass;
	}
}
