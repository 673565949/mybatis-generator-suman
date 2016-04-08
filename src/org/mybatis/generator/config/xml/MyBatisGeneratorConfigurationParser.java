/*
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
package org.mybatis.generator.config.xml;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;
import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.mybatis.generator.config.ColumnOverride;
import org.mybatis.generator.config.ColumnRenamingRule;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.IgnoredColumn;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.JavaTypeResolverConfiguration;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.PropertyHolder;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.ObjectFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class parses configuration files into the new Configuration API
 * 
 * @author Jeff Butler
 */
public class MyBatisGeneratorConfigurationParser {
	private Properties properties;

	public MyBatisGeneratorConfigurationParser(Properties properties) {
		super();
		if (properties == null) {
			this.properties = System.getProperties();
		} else {
			this.properties = properties;
		}
	}

	/**
	 * 解析配置文件
	 * 
	 * @param rootNode
	 * @return
	 * @throws XMLParserException
	 */
	public Configuration parseConfiguration(Element rootNode) throws XMLParserException {
		// 创建配置文件
		Configuration configuration = new Configuration();

		NodeList nodeList = rootNode.getChildNodes();// 得到子节点
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);// 遍历子节点

			if (childNode.getNodeType() != Node.ELEMENT_NODE) {// 如果不是ElementNode
				continue;
			}

			if ("properties".equals(childNode.getNodeName())) { //$NON-NLS-1$//如果节点名称是properties
				parseProperties(configuration, childNode);// 解析properties节点
			} else if ("classPathEntry".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseClassPathEntry(configuration, childNode);// 解析ClassPathEntry属性 读取jar包的路径
			} else if ("context".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseContext(configuration, childNode);// 解析context属性
			}
		}

		return configuration;
	}

	/**
	 * 解析properties节点
	 * 
	 * @param configuration
	 * @param node
	 * @throws XMLParserException
	 */
	private void parseProperties(Configuration configuration, Node node) throws XMLParserException {
		Properties attributes = parseAttributes(node);
		String resource = attributes.getProperty("resource"); //$NON-NLS-1$//得到resource
		String url = attributes.getProperty("url"); //$NON-NLS-1$//得到URL

		if (!stringHasValue(resource) && !stringHasValue(url)) {// 如果resource和url都是空的
			throw new XMLParserException(getString("RuntimeError.14")); //$NON-NLS-1$
		}

		if (stringHasValue(resource) && stringHasValue(url)) {// 如果都有值
			throw new XMLParserException(getString("RuntimeError.14")); //$NON-NLS-1$
		}

		URL resourceUrl;

		try {
			if (stringHasValue(resource)) {
				resourceUrl = ObjectFactory.getResource(resource);
				if (resourceUrl == null) {
					throw new XMLParserException(getString("RuntimeError.15", resource)); //$NON-NLS-1$
				}
			} else {
				resourceUrl = new URL(url);
			}

			InputStream inputStream = resourceUrl.openConnection().getInputStream();

			properties.load(inputStream);// 加载配置文件
			inputStream.close();
		} catch (IOException e) {
			if (stringHasValue(resource)) {
				throw new XMLParserException(getString("RuntimeError.16", resource)); //$NON-NLS-1$
			} else {
				throw new XMLParserException(getString("RuntimeError.17", url)); //$NON-NLS-1$
			}
		}
	}

	/**
	 * 解析context属性
	 * 
	 * @param configuration
	 * @param node
	 */
	private void parseContext(Configuration configuration, Node node) {

		Properties attributes = parseAttributes(node);
		String defaultModelType = attributes.getProperty("defaultModelType"); //得到defaultModelType属性
		String targetRuntime = attributes.getProperty("targetRuntime"); //得到targetRuntime属性
		String introspectedColumnImpl = attributes.getProperty("introspectedColumnImpl"); //得到introspectedColumnImpl属性
		String id = attributes.getProperty("id"); //得到id属性

		ModelType mt = defaultModelType == null ? null : ModelType.getModelType(defaultModelType);// 得到modelType

		Context context = new Context(mt);// 得到context对象
		context.setId(id);
		if (stringHasValue(introspectedColumnImpl)) {
			context.setIntrospectedColumnImpl(introspectedColumnImpl);
		}
		if (stringHasValue(targetRuntime)) {
			
			/*
			MyBatis3	这是默认值 
			使用这值的时候，MBG会生成兼容MyBatis 3.0或更高版本， 兼容JSE 5.0或更高版本的对象（例如Java模型类和Mapper接口会使用泛型）。 这些生成对象中的"by example"方法将支持几乎不受限制的动态的where子句。 另外，这些生成器生成的Java对象支持JSE 5.0特性，包含泛型和注解。
			MyBatis3Simple	这是默认值 
			使用这值的时候，和上面的MyBatis3类似，但是不会有"by example"一类的方法，只有少量的动态SQL。
			Ibatis2Java2	使用这值的时候，MBG会生成兼容iBATIS 2.2.0或更高版本（除了iBATIS 3），还有Java2的所有层次。 
			这些生成对象中的"by example"方法将支持几乎不受限制的动态的where子句。 这些生成的对象不能100%和原生的Abator或其他的代码生成器兼容。
			Ibatis2Java5	使用这值的时候，MBG会生成兼容iBATIS 2.2.0或更高版本（除了iBATIS 3）， 兼容JSE 5.0或更高版本的对象（例如Java模型类和Dao类会使用泛型）。 
			这些生成对象中的"by example"方法将支持几乎不受限制的动态的where子句。 另外，这些生成器生成的Java对象支持JSE 5.0特性，包含泛型和注解。 这些生成的对象不能100%和原生的Abator或其他的代码生成器兼容。
			*/
			context.setTargetRuntime(targetRuntime);
		}

		configuration.addContext(context);// 配置中添加context对象

		NodeList nodeList = node.getChildNodes();// 得到子节点
		for (int i = 0; i < nodeList.getLength(); i++) {//遍历context标签的所有子节点
			Node childNode = nodeList.item(i);

			if (childNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			if ("property".equals(childNode.getNodeName())) { //解析property属性
				parseProperty(context, childNode);
			} else if ("plugin".equals(childNode.getNodeName())) { //解析插件属性
				parsePlugin(context, childNode);
			} else if ("commentGenerator".equals(childNode.getNodeName())) { //解析注释生成配置
				parseCommentGenerator(context, childNode);
			} else if ("jdbcConnection".equals(childNode.getNodeName())) { //解析JDBC连接配置
				parseJdbcConnection(context, childNode);
			} else if ("javaModelGenerator".equals(childNode.getNodeName())) { //解析java模型生成配置
				parseJavaModelGenerator(context, childNode);
			} else if ("javaTypeResolver".equals(childNode.getNodeName())) { //解析java类型转换器
				parseJavaTypeResolver(context, childNode);
			} else if ("sqlMapGenerator".equals(childNode.getNodeName())) { //解析sqlMap（XML）文件生成配置
				parseSqlMapGenerator(context, childNode);
			} else if ("javaClientGenerator".equals(childNode.getNodeName())) { //解析 sqlMapper类生成配置
				parseJavaClientGenerator(context, childNode);
			} else if ("table".equals(childNode.getNodeName())) { //解析table配置
				parseTable(context, childNode);
			}
		}
	}

	private void parseSqlMapGenerator(Context context, Node node) {
		SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();

		context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

		Properties attributes = parseAttributes(node);
		String targetPackage = attributes.getProperty("targetPackage"); //$NON-NLS-1$
		String targetProject = attributes.getProperty("targetProject"); //$NON-NLS-1$

		sqlMapGeneratorConfiguration.setTargetPackage(targetPackage);
		sqlMapGeneratorConfiguration.setTargetProject(targetProject);

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);

			if (childNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseProperty(sqlMapGeneratorConfiguration, childNode);
			}
		}
	}

	private void parseTable(Context context, Node node) {
		TableConfiguration tc = new TableConfiguration(context);
		context.addTableConfiguration(tc);

		Properties attributes = parseAttributes(node);
		String catalog = attributes.getProperty("catalog"); //$NON-NLS-1$
		String schema = attributes.getProperty("schema"); //$NON-NLS-1$
		String tableName = attributes.getProperty("tableName"); //$NON-NLS-1$
		String domainObjectName = attributes.getProperty("domainObjectName"); //$NON-NLS-1$
		String alias = attributes.getProperty("alias"); //$NON-NLS-1$
		String enableInsert = attributes.getProperty("enableInsert"); //$NON-NLS-1$
		String enableSelectByPrimaryKey = attributes.getProperty("enableSelectByPrimaryKey"); //$NON-NLS-1$
		String enableSelectByExample = attributes.getProperty("enableSelectByExample"); //$NON-NLS-1$
		String enableUpdateByPrimaryKey = attributes.getProperty("enableUpdateByPrimaryKey"); //$NON-NLS-1$
		String enableDeleteByPrimaryKey = attributes.getProperty("enableDeleteByPrimaryKey"); //$NON-NLS-1$
		String enableDeleteByExample = attributes.getProperty("enableDeleteByExample"); //$NON-NLS-1$
		String enableCountByExample = attributes.getProperty("enableCountByExample"); //$NON-NLS-1$
		String enableUpdateByExample = attributes.getProperty("enableUpdateByExample"); //$NON-NLS-1$
		String selectByPrimaryKeyQueryId = attributes.getProperty("selectByPrimaryKeyQueryId"); //$NON-NLS-1$
		String selectByExampleQueryId = attributes.getProperty("selectByExampleQueryId"); //$NON-NLS-1$
		String modelType = attributes.getProperty("modelType"); //$NON-NLS-1$
		String escapeWildcards = attributes.getProperty("escapeWildcards"); //$NON-NLS-1$
		String delimitIdentifiers = attributes.getProperty("delimitIdentifiers"); //$NON-NLS-1$
		String delimitAllColumns = attributes.getProperty("delimitAllColumns"); //$NON-NLS-1$

		if (stringHasValue(catalog)) {
			tc.setCatalog(catalog);
		}

		if (stringHasValue(schema)) {
			tc.setSchema(schema);
		}

		if (stringHasValue(tableName)) {
			tc.setTableName(tableName);
		}

		if (stringHasValue(domainObjectName)) {
			tc.setDomainObjectName(domainObjectName);
		}

		if (stringHasValue(alias)) {
			tc.setAlias(alias);
		}

		if (stringHasValue(enableInsert)) {
			tc.setInsertStatementEnabled(isTrue(enableInsert));
		}

		if (stringHasValue(enableSelectByPrimaryKey)) {
			tc.setSelectByPrimaryKeyStatementEnabled(isTrue(enableSelectByPrimaryKey));
		}

		if (stringHasValue(enableSelectByExample)) {
			tc.setSelectByExampleStatementEnabled(isTrue(enableSelectByExample));
		}

		if (stringHasValue(enableUpdateByPrimaryKey)) {
			tc.setUpdateByPrimaryKeyStatementEnabled(isTrue(enableUpdateByPrimaryKey));
		}

		if (stringHasValue(enableDeleteByPrimaryKey)) {
			tc.setDeleteByPrimaryKeyStatementEnabled(isTrue(enableDeleteByPrimaryKey));
		}

		if (stringHasValue(enableDeleteByExample)) {
			tc.setDeleteByExampleStatementEnabled(isTrue(enableDeleteByExample));
		}

		if (stringHasValue(enableCountByExample)) {
			tc.setCountByExampleStatementEnabled(isTrue(enableCountByExample));
		}

		if (stringHasValue(enableUpdateByExample)) {
			tc.setUpdateByExampleStatementEnabled(isTrue(enableUpdateByExample));
		}

		if (stringHasValue(selectByPrimaryKeyQueryId)) {
			tc.setSelectByPrimaryKeyQueryId(selectByPrimaryKeyQueryId);
		}

		if (stringHasValue(selectByExampleQueryId)) {
			tc.setSelectByExampleQueryId(selectByExampleQueryId);
		}

		if (stringHasValue(modelType)) {
			tc.setConfiguredModelType(modelType);
		}

		if (stringHasValue(escapeWildcards)) {
			tc.setWildcardEscapingEnabled(isTrue(escapeWildcards));
		}

		if (stringHasValue(delimitIdentifiers)) {
			tc.setDelimitIdentifiers(isTrue(delimitIdentifiers));
		}

		if (stringHasValue(delimitAllColumns)) {//是否分隔全部的列
			tc.setAllColumnDelimitingEnabled(isTrue(delimitAllColumns));//设置分隔
		}

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);

			if (childNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseProperty(tc, childNode);
			} else if ("columnOverride".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseColumnOverride(tc, childNode);
			} else if ("ignoreColumn".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseIgnoreColumn(tc, childNode);
			} else if ("generatedKey".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseGeneratedKey(tc, childNode);
			} else if ("columnRenamingRule".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseColumnRenamingRule(tc, childNode);
			}
		}
	}

	private void parseColumnOverride(TableConfiguration tc, Node node) {
		Properties attributes = parseAttributes(node);
		String column = attributes.getProperty("column"); //$NON-NLS-1$
		String property = attributes.getProperty("property"); //$NON-NLS-1$
		String javaType = attributes.getProperty("javaType"); //$NON-NLS-1$
		String jdbcType = attributes.getProperty("jdbcType"); //$NON-NLS-1$
		String typeHandler = attributes.getProperty("typeHandler"); //$NON-NLS-1$
		String delimitedColumnName = attributes.getProperty("delimitedColumnName"); //$NON-NLS-1$

		ColumnOverride co = new ColumnOverride(column);

		if (stringHasValue(property)) {
			co.setJavaProperty(property);
		}

		if (stringHasValue(javaType)) {
			co.setJavaType(javaType);
		}

		if (stringHasValue(jdbcType)) {
			co.setJdbcType(jdbcType);
		}

		if (stringHasValue(typeHandler)) {
			co.setTypeHandler(typeHandler);
		}

		if (stringHasValue(delimitedColumnName)) {
			co.setColumnNameDelimited(isTrue(delimitedColumnName));
		}

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);

			if (childNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseProperty(co, childNode);
			}
		}

		tc.addColumnOverride(co);
	}

	private void parseGeneratedKey(TableConfiguration tc, Node node) {
		Properties attributes = parseAttributes(node);

		String column = attributes.getProperty("column"); //$NON-NLS-1$
		boolean identity = isTrue(attributes.getProperty("identity")); //$NON-NLS-1$
		String sqlStatement = attributes.getProperty("sqlStatement"); //$NON-NLS-1$
		String type = attributes.getProperty("type"); //$NON-NLS-1$

		GeneratedKey gk = new GeneratedKey(column, sqlStatement, identity, type);

		tc.setGeneratedKey(gk);
	}

	private void parseIgnoreColumn(TableConfiguration tc, Node node) {
		Properties attributes = parseAttributes(node);
		String column = attributes.getProperty("column"); //$NON-NLS-1$
		String delimitedColumnName = attributes.getProperty("delimitedColumnName"); //$NON-NLS-1$

		IgnoredColumn ic = new IgnoredColumn(column);

		if (stringHasValue(delimitedColumnName)) {
			ic.setColumnNameDelimited(isTrue(delimitedColumnName));
		}

		tc.addIgnoredColumn(ic);
	}

	private void parseColumnRenamingRule(TableConfiguration tc, Node node) {
		Properties attributes = parseAttributes(node);
		String searchString = attributes.getProperty("searchString"); //$NON-NLS-1$
		String replaceString = attributes.getProperty("replaceString"); //$NON-NLS-1$

		ColumnRenamingRule crr = new ColumnRenamingRule();

		crr.setSearchString(searchString);

		if (stringHasValue(replaceString)) {
			crr.setReplaceString(replaceString);
		}

		tc.setColumnRenamingRule(crr);
	}

	private void parseJavaTypeResolver(Context context, Node node) {
		JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();

		context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);

		Properties attributes = parseAttributes(node);
		String type = attributes.getProperty("type"); //$NON-NLS-1$

		if (stringHasValue(type)) {
			javaTypeResolverConfiguration.setConfigurationType(type);
		}

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);

			if (childNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseProperty(javaTypeResolverConfiguration, childNode);
			}
		}
	}
	/**
	 * 解析插件节点
	 * 实现该接口的类的完全限定名的插件。
	 * 该类必须实现该接口 org.mybatis.generator.api.Plugin,
	 * 必须有一个公开默认的构造函数。
	 * 注意，继承 org.mybatis.generator.api.PluginAdapter 这个适配器类比继承接口更容易扩展。
	 * @param context
	 * @param node
	 */
	private void parsePlugin(Context context, Node node) {// 解析插件
		PluginConfiguration pluginConfiguration = new PluginConfiguration();

		context.addPluginConfiguration(pluginConfiguration);//将插件配置添加到context中

		Properties attributes = parseAttributes(node);
		String type = attributes.getProperty("type"); //得到Plugin的Type

		pluginConfiguration.setConfigurationType(type);

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);

			if (childNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			if ("property".equals(childNode.getNodeName())) { //遍历property子节点并存入到插件配置对象中
				parseProperty(pluginConfiguration, childNode);
			}
		}
	}

	private void parseJavaModelGenerator(Context context, Node node) {
		JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();

		context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

		Properties attributes = parseAttributes(node);
		String targetPackage = attributes.getProperty("targetPackage"); //解析生成的Model类的包名
		String targetProject = attributes.getProperty("targetProject"); //生成到的项目位置 在该位置下生成java的包文件夹和类文件

		javaModelGeneratorConfiguration.setTargetPackage(targetPackage);
		javaModelGeneratorConfiguration.setTargetProject(targetProject);

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);

			if (childNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseProperty(javaModelGeneratorConfiguration, childNode);
			}
		}
	}

	private void parseJavaClientGenerator(Context context, Node node) {
		JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();

		context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

		Properties attributes = parseAttributes(node);
		String type = attributes.getProperty("type"); //$NON-NLS-1$
		String targetPackage = attributes.getProperty("targetPackage"); //$NON-NLS-1$
		String targetProject = attributes.getProperty("targetProject"); //$NON-NLS-1$
		String implementationPackage = attributes.getProperty("implementationPackage"); //$NON-NLS-1$

		javaClientGeneratorConfiguration.setConfigurationType(type);
		javaClientGeneratorConfiguration.setTargetPackage(targetPackage);
		javaClientGeneratorConfiguration.setTargetProject(targetProject);
		javaClientGeneratorConfiguration.setImplementationPackage(implementationPackage);

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);

			if (childNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseProperty(javaClientGeneratorConfiguration, childNode);
			}
		}
	}
	/**
	 * 解析jdbc连接配置
	 * @param context
	 * @param node
	 */
	private void parseJdbcConnection(Context context, Node node) {
		JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();

		context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

		Properties attributes = parseAttributes(node);
		String driverClass = attributes.getProperty("driverClass"); //
		String connectionURL = attributes.getProperty("connectionURL"); //$NON-NLS-1$
		String userId = attributes.getProperty("userId"); //$NON-NLS-1$
		String password = attributes.getProperty("password"); //$NON-NLS-1$

		jdbcConnectionConfiguration.setDriverClass(driverClass);
		jdbcConnectionConfiguration.setConnectionURL(connectionURL);

		if (stringHasValue(userId)) {
			jdbcConnectionConfiguration.setUserId(userId);
		}

		if (stringHasValue(password)) {
			jdbcConnectionConfiguration.setPassword(password);
		}

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);

			if (childNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseProperty(jdbcConnectionConfiguration, childNode);
			}
		}
	}

	/***
	 * 解析ClassPathEntry属性
	 * 也就是jdbc Jar包 的路径
	 * @param configuration
	 * @param node
	 */
	private void parseClassPathEntry(Configuration configuration, Node node) {
		Properties attributes = parseAttributes(node);// 解析属性值

		configuration.addClasspathEntry(attributes.getProperty("location")); //$NON-NLS-1$

	}

	private void parseProperty(PropertyHolder propertyHolder, Node node) {
		Properties attributes = parseAttributes(node);

		String name = attributes.getProperty("name"); //$NON-NLS-1$
		String value = attributes.getProperty("value"); //$NON-NLS-1$

		propertyHolder.addProperty(name, value);
	}

	private Properties parseAttributes(Node node) {
		Properties attributes = new Properties();
		NamedNodeMap nnm = node.getAttributes();// 得到属性
		for (int i = 0; i < nnm.getLength(); i++) {
			Node attribute = nnm.item(i);
			String value = parsePropertyTokens(attribute.getNodeValue());
			attributes.put(attribute.getNodeName(), value);
		}

		return attributes;
	}

	private String parsePropertyTokens(String string) {
		final String OPEN = "${"; //$NON-NLS-1$
		final String CLOSE = "}"; //$NON-NLS-1$

		String newString = string;
		if (newString != null) {
			int start = newString.indexOf(OPEN);
			int end = newString.indexOf(CLOSE);

			while (start > -1 && end > start) {
				String prepend = newString.substring(0, start);
				String append = newString.substring(end + CLOSE.length());
				String propName = newString.substring(start + OPEN.length(), end);
				String propValue = properties.getProperty(propName);
				if (propValue != null) {
					newString = prepend + propValue + append;
				}

				start = newString.indexOf(OPEN, end);
				end = newString.indexOf(CLOSE, end);
			}
		}

		return newString;
	}
	/**
	 * 解析注释生成配置
	 * <!-- 是否去除自动生成的时间戳注释：是 ： false:否 -->
	 * <property name="suppressDate" value="true"/>
	 *<!-- 是否去除自动生成的注释 true：是 ： false:否 -->
	 *<property name="suppressAllComments" value="true"/>
	 * @param context
	 * @param node
	 */
	private void parseCommentGenerator(Context context, Node node) {
		CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();

		context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);

		Properties attributes = parseAttributes(node);
		String type = attributes.getProperty("type"); //$NON-NLS-1$

		if (stringHasValue(type)) {
			commentGeneratorConfiguration.setConfigurationType(type);
		}

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);

			if (childNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			if ("property".equals(childNode.getNodeName())) { //$NON-NLS-1$
				parseProperty(commentGeneratorConfiguration, childNode);
			}
		}
	}
}
