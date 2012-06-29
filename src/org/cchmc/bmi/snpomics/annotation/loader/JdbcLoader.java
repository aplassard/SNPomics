package org.cchmc.bmi.snpomics.annotation.loader;

import java.sql.Connection;

import org.cchmc.bmi.snpomics.annotation.ReferenceAnnotation;

public abstract class JdbcLoader<T extends ReferenceAnnotation> implements AnnotationLoader<T>{
	public void setTableName(String table) { 
		tableName = table; 
	}
	public void setConnection(Connection cxn) {
		connection = cxn;
	}

	protected String tableName;
	protected Connection connection;
}
