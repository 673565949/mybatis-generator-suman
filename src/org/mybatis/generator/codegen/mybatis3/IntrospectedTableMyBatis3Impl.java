/*
 *  Copyright 2009 The Apache Software Foundation
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
package org.mybatis.generator.codegen.mybatis3;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.codegen.AbstractGenerator;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.controller.ControllerBaseGenerator;
import org.mybatis.generator.codegen.mybatis3.controller.ControllerWebGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.AnnotatedClientGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.JavaMapperGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.MixedClientGenerator;
import org.mybatis.generator.codegen.mybatis3.model.BaseExampleGenerator;
import org.mybatis.generator.codegen.mybatis3.model.BaseRecordGenerator;
import org.mybatis.generator.codegen.mybatis3.model.ExampleGenerator;
import org.mybatis.generator.codegen.mybatis3.model.PrimaryKeyGenerator;
import org.mybatis.generator.codegen.mybatis3.model.RecordWithBLOBsGenerator;
import org.mybatis.generator.codegen.mybatis3.service.ServiceImplGenerator;
import org.mybatis.generator.codegen.mybatis3.service.ServiceInterfaceGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.XMLMapperGenerator;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.ObjectFactory;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

/**
 * The Class IntrospectedTableMyBatis3Impl.
 * 
 * @author Jeff Butler
 */
public class IntrospectedTableMyBatis3Impl extends IntrospectedTable {

	/** The java model generators. */
	protected List<AbstractJavaGenerator> javaModelGenerators;

	/** The client generators. */
	protected List<AbstractJavaGenerator> clientGenerators;

	/** The xml mapper generator. */
	protected AbstractXmlGenerator xmlMapperGenerator;
	
	protected List<AbstractJavaGenerator> serviceInterfaceGenerators;

	protected List<AbstractJavaGenerator> serviceImplGenerators;
	protected List<AbstractJavaGenerator> controllerBaseGenerators;
	protected List<AbstractJavaGenerator> controllerWebGenerators;
	
	/**
	 * Instantiates a new introspected table my batis3 impl.
	 */
	public IntrospectedTableMyBatis3Impl() {
		super(TargetRuntime.MYBATIS3);
		javaModelGenerators = new ArrayList<AbstractJavaGenerator>();
		clientGenerators = new ArrayList<AbstractJavaGenerator>();
		serviceInterfaceGenerators = new ArrayList<AbstractJavaGenerator>();
		serviceImplGenerators = new ArrayList<AbstractJavaGenerator>();
		controllerBaseGenerators = new ArrayList<AbstractJavaGenerator>();
		controllerWebGenerators = new ArrayList<AbstractJavaGenerator>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mybatis.generator.api.IntrospectedTable#calculateGenerators(java.
	 * util.List, org.mybatis.generator.api.ProgressCallback)
	 */
	@Override
	public void calculateGenerators(List<String> warnings, ProgressCallback progressCallback) {
		calculateJavaModelGenerators(warnings, progressCallback);// �������� java
																	// Model��������

		AbstractJavaClientGenerator javaClientGenerator = calculateClientGenerators(warnings, progressCallback);// ��������
																												// java
																												// Client��������
		calculateServiceInterfaceGenerators(warnings, progressCallback);// ��������
		calculateServiceImplGenerators(warnings, progressCallback);// ��������
		calculateControllerBaseGenerators(warnings, progressCallback);
		calculateControllerWebGenerators(warnings, progressCallback);
		calculateXmlMapperGenerator(javaClientGenerator, warnings, progressCallback);
	}

	/**
	 * Calculate xml mapper generator.
	 * 
	 * @param javaClientGenerator
	 *            the java client generator
	 * @param warnings
	 *            the warnings
	 * @param progressCallback
	 *            the progress callback
	 */
	protected void calculateXmlMapperGenerator(AbstractJavaClientGenerator javaClientGenerator, List<String> warnings, ProgressCallback progressCallback) {
		if (javaClientGenerator == null) {// ���javaClientGenerator�ǿյ�
											// Ҳ���ǲ�����javaClientGenerator
											// ʹ��XMLMapperGenerator ����xml�ļ�
			if (context.getSqlMapGeneratorConfiguration() != null) {
				xmlMapperGenerator = new XMLMapperGenerator();
			}
		} else {
			xmlMapperGenerator = javaClientGenerator.getMatchedXMLGenerator();// ���javaClientGenerator���ǿյ�
																				// ��ô�õ���Ӧ��xml������
		}

		initializeAbstractGenerator(xmlMapperGenerator, warnings, progressCallback);// ��ʼ��
	}

	/**
	 * Calculate client generators.
	 * 
	 * @param warnings
	 *            the warnings
	 * @param progressCallback
	 *            the progress callback
	 * @return true if an XML generator is required
	 */
	protected AbstractJavaClientGenerator calculateClientGenerators(List<String> warnings, ProgressCallback progressCallback) {
		if (!rules.generateJavaClient()) {
			return null;
		}

		AbstractJavaClientGenerator javaGenerator = createJavaClientGenerator();
		if (javaGenerator == null) {
			return null;
		}

		initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
		clientGenerators.add(javaGenerator);

		return javaGenerator;
	}

	/**
	 * Calculate client generators.
	 * 
	 * @param warnings
	 *            the warnings
	 * @param progressCallback
	 *            the progress callback
	 * @return true if an XML generator is required
	 */
	protected AbstractJavaClientGenerator calculateServiceInterfaceGenerators(List<String> warnings, ProgressCallback progressCallback) {

		AbstractJavaClientGenerator javaGenerator = createServiceInterfaceGenerator();
		if (javaGenerator == null) {
			return null;
		}

		initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
		serviceInterfaceGenerators.add(javaGenerator);

		return javaGenerator;
	}

	
	/**
	 * Calculate client generators.
	 * 
	 * @param warnings
	 *            the warnings
	 * @param progressCallback
	 *            the progress callback
	 * @return true if an XML generator is required
	 */
	protected AbstractJavaGenerator calculateServiceImplGenerators(List<String> warnings, ProgressCallback progressCallback) {

		AbstractJavaGenerator javaGenerator = createServiceImplGenerator();
		if (javaGenerator == null) {
			return null;
		}

		initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
		serviceImplGenerators.add(javaGenerator);

		return javaGenerator;
	}

	/**
	 * Calculate client generators.
	 * 
	 * @param warnings
	 *            the warnings
	 * @param progressCallback
	 *            the progress callback
	 * @return true if an XML generator is required
	 */
	protected AbstractJavaGenerator calculateControllerBaseGenerators(List<String> warnings, ProgressCallback progressCallback) {

		AbstractJavaGenerator javaGenerator = createControllerBaseGenerator();
		if (javaGenerator == null) {
			return null;
		}

		initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
		controllerBaseGenerators.add(javaGenerator);

		return javaGenerator;
	}
	protected AbstractJavaGenerator calculateControllerWebGenerators(List<String> warnings, ProgressCallback progressCallback) {

		AbstractJavaGenerator javaGenerator = createControllerWebGenerator();
		if (javaGenerator == null) {
			return null;
		}

		initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
		controllerWebGenerators.add(javaGenerator);

		return javaGenerator;
	}
	/**
	 * Creates the java client generator.
	 * 
	 * @return the abstract java client generator
	 */
	protected AbstractJavaClientGenerator createJavaClientGenerator() {
		if (context.getJavaClientGeneratorConfiguration() == null) {
			return null;
		}

		String type = context.getJavaClientGeneratorConfiguration().getConfigurationType();

		AbstractJavaClientGenerator javaGenerator;
		if ("XMLMAPPER".equalsIgnoreCase(type)) { //$NON-NLS-1$
			javaGenerator = new JavaMapperGenerator();// XML�ļ�ģʽ
		} else if ("MIXEDMAPPER".equalsIgnoreCase(type)) { //$NON-NLS-1$
			javaGenerator = new MixedClientGenerator();// ���ģʽ
		} else if ("ANNOTATEDMAPPER".equalsIgnoreCase(type)) { //$NON-NLS-1$
			javaGenerator = new AnnotatedClientGenerator();// ע��ģʽ
		} else if ("MAPPER".equalsIgnoreCase(type)) { //$NON-NLS-1$
			javaGenerator = new JavaMapperGenerator();
		} else {
			javaGenerator = (AbstractJavaClientGenerator) ObjectFactory.createInternalObject(type);
		}

		return javaGenerator;
	}
	/**
	 * Creates the java client generator.
	 * 
	 * @return the abstract java client generator
	 */
	protected AbstractJavaClientGenerator createServiceInterfaceGenerator() {
		AbstractJavaClientGenerator javaGenerator = new ServiceInterfaceGenerator();
		return javaGenerator;
	}
	/**
	 * Creates the java client generator.
	 * 
	 * @return the abstract java client generator
	 */
	protected AbstractJavaGenerator createServiceImplGenerator() {
		AbstractJavaGenerator javaGenerator = new ServiceImplGenerator();
		return javaGenerator;
	}
	
	protected AbstractJavaGenerator createControllerBaseGenerator() {
		AbstractJavaGenerator javaGenerator = new ControllerBaseGenerator();
		return javaGenerator;
	}
	protected AbstractJavaGenerator createControllerWebGenerator() {
		AbstractJavaGenerator javaGenerator = new ControllerWebGenerator();
		return javaGenerator;
	}

	/**
	 * Calculate java model generators.
	 * 
	 * @param warnings
	 *            the warnings
	 * @param progressCallback
	 *            the progress callback
	 */
	protected void calculateJavaModelGenerators(List<String> warnings, ProgressCallback progressCallback) {
		if (getRules().generateExampleClass()) {// �Ƿ�����example��
			AbstractJavaGenerator javaGenerator = new ExampleGenerator();
			initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
			javaModelGenerators.add(javaGenerator);
		}

		if (getRules().generatePrimaryKeyClass()) {// �Ƿ�����������
			AbstractJavaGenerator javaGenerator = new PrimaryKeyGenerator();
			initializeAbstractGenerator(javaGenerator, warnings, progressCallback);// ��ʼ������ν�ĳ�ʼ�����ǰ���Ҫ��ֵ���ø�javaGenerator
																					// ����context���ð�
																					// �����ð�ʲô��
			javaModelGenerators.add(javaGenerator);
		}

		if (getRules().generateBaseRecordClass()) {// �Ƿ�����baseRecord��
			AbstractJavaGenerator javaGenerator = new BaseRecordGenerator();
			initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
			javaModelGenerators.add(javaGenerator);
		}

		if (getRules().generateRecordWithBLOBsClass()) {// ����blob��
			AbstractJavaGenerator javaGenerator = new RecordWithBLOBsGenerator();
			initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
			javaModelGenerators.add(javaGenerator);
		}
	}

	/**
	 * Initialize abstract generator.
	 * 
	 * @param abstractGenerator
	 *            the abstract generator
	 * @param warnings
	 *            the warnings
	 * @param progressCallback
	 *            the progress callback
	 */
	protected void initializeAbstractGenerator(AbstractGenerator abstractGenerator, List<String> warnings, ProgressCallback progressCallback) {
		if (abstractGenerator == null) {
			return;
		}

		abstractGenerator.setContext(context);
		abstractGenerator.setIntrospectedTable(this);
		abstractGenerator.setProgressCallback(progressCallback);
		abstractGenerator.setWarnings(warnings);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mybatis.generator.api.IntrospectedTable#getGeneratedJavaFiles()
	 */
	@Override
	public List<GeneratedJavaFile> getGeneratedJavaFiles() {
		List<GeneratedJavaFile> answer = new ArrayList<GeneratedJavaFile>();

		for (AbstractJavaGenerator javaGenerator : javaModelGenerators) {
			List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits();
			for (CompilationUnit compilationUnit : compilationUnits) {
				GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit, context.getJavaModelGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), context.getJavaFormatter());
				answer.add(gjf);
			}
		}

		for (AbstractJavaGenerator javaGenerator : clientGenerators) {
			List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits();
			for (CompilationUnit compilationUnit : compilationUnits) {
				GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit, context.getJavaClientGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), context.getJavaFormatter());
				answer.add(gjf);
			}
		}
		
		for (AbstractJavaGenerator javaGenerator : serviceInterfaceGenerators) {
			List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits();
			for (CompilationUnit compilationUnit : compilationUnits) {
				GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit, context.getJavaClientGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), context.getJavaFormatter());
				answer.add(gjf);
			}
		}
		for (AbstractJavaGenerator javaGenerator : serviceImplGenerators) {
			List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits();
			for (CompilationUnit compilationUnit : compilationUnits) {
				GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit, context.getJavaClientGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), context.getJavaFormatter());
				answer.add(gjf);
			}
		}
		
		for (AbstractJavaGenerator javaGenerator : controllerBaseGenerators) {
			List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits();
			for (CompilationUnit compilationUnit : compilationUnits) {
				GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit, context.getJavaClientGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), context.getJavaFormatter());
				answer.add(gjf);
			}
		}

		for (AbstractJavaGenerator javaGenerator : controllerWebGenerators) {
			List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits();
			for (CompilationUnit compilationUnit : compilationUnits) {
				GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit, context.getJavaClientGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), context.getJavaFormatter());
				answer.add(gjf);
			}
		}

		return answer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mybatis.generator.api.IntrospectedTable#getGeneratedXmlFiles()
	 */
	@Override
	public List<GeneratedXmlFile> getGeneratedXmlFiles() {
		List<GeneratedXmlFile> answer = new ArrayList<GeneratedXmlFile>();

		if (xmlMapperGenerator != null) {
			Document document = xmlMapperGenerator.getDocument();
			GeneratedXmlFile gxf = new GeneratedXmlFile(document, getMyBatis3XmlMapperFileName(), getMyBatis3XmlMapperPackage(), context.getSqlMapGeneratorConfiguration().getTargetProject(), isTrue(context.getProperty(PropertyRegistry.CONTEXT_XML_MREGEABLE)), context.getXmlFormatter());// change
																																																																								// by
																																																																								// suman
																																																																								// new
			/* true, context.getXmlFormatter());//change by suman old */

			if (context.getPlugins().sqlMapGenerated(gxf, this)) {
				answer.add(gxf);
			}
		}

		return answer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mybatis.generator.api.IntrospectedTable#getGenerationSteps()
	 */
	@Override
	public int getGenerationSteps() {
		return javaModelGenerators.size() + clientGenerators.size()+serviceInterfaceGenerators.size() + (xmlMapperGenerator == null ? 0 : 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mybatis.generator.api.IntrospectedTable#isJava5Targeted()
	 */
	@Override
	public boolean isJava5Targeted() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mybatis.generator.api.IntrospectedTable#requiresXMLGenerator()
	 */
	@Override
	public boolean requiresXMLGenerator() {
		AbstractJavaClientGenerator javaClientGenerator = createJavaClientGenerator();

		if (javaClientGenerator == null) {
			return false;
		} else {
			return javaClientGenerator.requiresXMLGenerator();
		}
	}
}
