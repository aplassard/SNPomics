package org.cchmc.bmi.snpomics.annotation.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.cchmc.bmi.snpomics.annotation.GenomicSequenceAnnotation;
import org.cchmc.bmi.snpomics.exception.UncheckedSnpomicsException;

public class GenomicSequenceImporter extends
		JdbcImporter<GenomicSequenceAnnotation> {

	@Override
	public boolean importAnnotations(Reader input) {
		createTable();
		BufferedReader reader = new BufferedReader(input);
		String filename;
		try {
			filename = reader.readLine();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		File providedFile = new File(filename);
		if (providedFile.isAbsolute()) {
			throw new UncheckedSnpomicsException("Absolute pathnames not permitted, path should be relative to dir specified in fastapath");
		}
		PreparedStatement stat = null;
		try {
			stat = connection.prepareStatement("INSERT INTO `"+tableName+"` VALUES (?)");
			stat.setString(1, filename);
			stat.execute();
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
	protected String tableCreationStmt() {
		return "CREATE TABLE `"+tableName+"` ("+
			"filename varchar(255) NOT NULL)";
	}

}
