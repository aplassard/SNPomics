package org.cchmc.bmi.snpomics.annotation.importer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.cchmc.bmi.snpomics.annotation.reference.ReferenceAnnotation;
import org.cchmc.bmi.snpomics.util.FastaReader;

public abstract class JdbcImporter<T extends ReferenceAnnotation> implements
		AnnotationImporter<T> {
	public void setTableName(String table) { 
		tableName = table; 
	}
	public void setConnection(Connection cxn) {
		connection = cxn;
	}
	
	protected abstract String tableCreationStmt();
	
	protected boolean createTable() {
		Statement stat = null;
		try {
			stat = connection.createStatement();
			stat.executeUpdate(tableCreationStmt());
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (stat != null)
					stat.close();
			} catch (SQLException e) {}
		}
		return true;

	}
	
	@Override
	public void setFastaReader(FastaReader fasta) {
		this.fasta = fasta;
	}

	protected String tableName;
	protected Connection connection;
	protected FastaReader fasta=null;
}
