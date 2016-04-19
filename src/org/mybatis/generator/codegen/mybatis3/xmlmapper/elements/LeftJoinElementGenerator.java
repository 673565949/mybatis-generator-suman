package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

import java.util.List;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

//add by suman
public class LeftJoinElementGenerator extends AbstractXmlElementGenerator {

	@Override
	public void addElements(XmlElement parentElement) {
		XmlElement answer = new XmlElement("sql");
		answer.addAttribute(new Attribute("id", introspectedTable.getLeftJoinListId()));

		List<IntrospectedColumn> columns = introspectedTable.getBaseColumns();

		for (IntrospectedColumn introspectedColumn : columns) {

			IntrospectedColumn introspectedImportColumn = introspectedColumn.getIntrospectedImportColumn();
			if (introspectedImportColumn == null) {
				continue;
			}
			IntrospectedTable introspectedImportTable = introspectedImportColumn.getIntrospectedTable();
			StringBuffer sb = new StringBuffer();
			sb.append("left join ");
			sb.append(introspectedImportTable.getAliasedFullyQualifiedTableNameAtRuntime());
			sb.append(" on ");
			sb.append( MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedImportColumn));
			sb.append(" = ");
			sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
			
			answer.addElement(new TextElement(sb.toString()));

		}
	
		
		context.getCommentGenerator().addComment(answer);
		if (context.getPlugins().sqlMapLeftJoinElementGenerated(answer, introspectedTable)) {
			parentElement.addElement(answer);
		}

	}

}
