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

import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.exception.XMLParserException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * 配置文件 解析类
 * 
 * @author perfect
 * 
 */
public class ConfigurationParser {

	private List<String> warnings;
	private List<String> parseErrors;
	private Properties properties;

	public ConfigurationParser(List<String> warnings) {
		this(null, warnings);
	}

	public ConfigurationParser(Properties properties, List<String> warnings) {
		super();
		if (properties == null) {
			this.properties = System.getProperties();
		} else {
			this.properties = properties;
		}

		if (warnings == null) {
			this.warnings = new ArrayList<String>();
		} else {
			this.warnings = warnings;
		}

		parseErrors = new ArrayList<String>();
	}

	public Configuration parseConfiguration(File inputFile) throws IOException, XMLParserException {

		FileReader fr = new FileReader(inputFile);

		return parseConfiguration(fr);
	}

	public Configuration parseConfiguration(Reader reader) throws IOException, XMLParserException {

		InputSource is = new InputSource(reader);

		return parseConfiguration(is);
	}

	/**
	 * 解析配置文件
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 * @throws XMLParserException
	 */
	public Configuration parseConfiguration(InputStream inputStream) throws IOException, XMLParserException {

		InputSource is = new InputSource(inputStream);

		return parseConfiguration(is);
	}

	/**
	 * 解析配置文件
	 * 
	 * @param inputSource
	 * @return
	 * @throws IOException
	 * @throws XMLParserException
	 */
	private Configuration parseConfiguration(InputSource inputSource) throws IOException, XMLParserException {
		parseErrors.clear();//清空解析异常
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new ParserEntityResolver());

			ParserErrorHandler handler = new ParserErrorHandler(warnings, parseErrors);// 解析错误处理器
			builder.setErrorHandler(handler);

			Document document = null;
			try {
				document = builder.parse(inputSource);// 得到document
			} catch (SAXParseException e) {
				throw new XMLParserException(parseErrors);
			} catch (SAXException e) {
				if (e.getException() == null) {
					parseErrors.add(e.getMessage());
				} else {
					parseErrors.add(e.getException().getMessage());
				}
			}

			if (parseErrors.size() > 0) {
				throw new XMLParserException(parseErrors);
			}

			Configuration config;
			Element rootNode = document.getDocumentElement();// 得到主节点
			DocumentType docType = document.getDoctype();// 得到文档类型
			if (rootNode.getNodeType() == Node.ELEMENT_NODE && docType.getPublicId().equals(XmlConstants.IBATOR_CONFIG_PUBLIC_ID)) {// 如果是ibatis文档
				config = parseIbatorConfiguration(rootNode);
			} else if (rootNode.getNodeType() == Node.ELEMENT_NODE && docType.getPublicId().equals(XmlConstants.MYBATIS_GENERATOR_CONFIG_PUBLIC_ID)) {// 如果是mybatis文档
				config = parseMyBatisGeneratorConfiguration(rootNode);
			} else {
				throw new XMLParserException(getString("RuntimeError.5")); //$NON-NLS-1$
			}

			if (parseErrors.size() > 0) {
				throw new XMLParserException(parseErrors);
			}

			return config;
		} catch (ParserConfigurationException e) {
			parseErrors.add(e.getMessage());
			throw new XMLParserException(parseErrors);
		}
	}

	private Configuration parseIbatorConfiguration(Element rootNode) throws XMLParserException {
		IbatorConfigurationParser parser = new IbatorConfigurationParser(properties);
		return parser.parseIbatorConfiguration(rootNode);
	}

	/*** 
	 * 解析mybatis格式配置文件
	 * 
	 * @param rootNode
	 * @return
	 * @throws XMLParserException
	 */
	private Configuration parseMyBatisGeneratorConfiguration(Element rootNode) throws XMLParserException {
		MyBatisGeneratorConfigurationParser parser = new MyBatisGeneratorConfigurationParser(properties);
		return parser.parseConfiguration(rootNode);
	}
}
