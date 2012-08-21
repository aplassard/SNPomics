package org.cchmc.bmi.snpomics.annotation.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;


import org.cchmc.bmi.snpomics.GenomicSpan;
import org.cchmc.bmi.snpomics.annotation.reference.TranscriptAnnotation;


public class TFBSImporter extends JdbcImporter<TranscriptAnnotation>  {
	
	private int n;
	private boolean initialized;
	
	@Override
	public boolean importAnnotations(Reader input) {
		if (initialized){
			createTable();
			BufferedReader reader = new BufferedReader(input);
			PreparedStatement stat = null;
			String line;
			try {
				stat = connection.prepareStatement(tableInsertStmt());
				while ((line = reader.readLine()) != null) {
					if (line.charAt(0) == '#')
						continue;
					String[] F = line.split("\t");
					GenomicSpan span = new GenomicSpan(F[0], Long.parseLong(F[1]), Long.parseLong(F[2]));
					stat.setInt(1, span.getBin());
					stat.setString(2, getPadded());
					stat.setString(3, F[0]);
					stat.setString(4, F[5]);
					stat.setLong(5, Long.parseLong(F[1]));
					stat.setLong(6, Long.parseLong(F[2]));
					stat.setString(7, F[3]);
					stat.execute();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
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
		else return false;
	}

	@Override
	protected String tableCreationStmt() {
		return "CREATE TABLE `"+tableName+"` ("+
				"bin smallint(5) UNSIGNED NOT NULL, "+
				"id varchar(30) NOT NULL, "+
				"chrom varchar(30) NOT NULL, "+
				"strand char(1) NOT NULL, "+
				"Start int(10) UNSIGNED NOT NULL, "+
				"End int(10) UNSIGNED NOT NULL, "+
				"Matrix varchar(30), " +
				"PRIMARY KEY (id), "+
				"INDEX bin (chrom, bin))";
	}

	private String tableInsertStmt() {
		return "INSERT INTO `"+tableName+"` SET "+
			"bin=?, id=?, chrom=?, strand=?, Start=?, End=?, Matrix=?";
	}
	
	private String getPadded(){
		String TFBS = "TFBS";
		String num = Integer.toString(n);
		n++;
		int length = num.length();
		for(int i = 0; i < 9-length;i++) num = "0"+num;
		return TFBS+"_"+num;
	}
	
	public void setN(int num) {
		initialized=true;
		n=num;
	}
}
