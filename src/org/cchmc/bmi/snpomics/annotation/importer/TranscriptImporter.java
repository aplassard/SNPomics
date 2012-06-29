package org.cchmc.bmi.snpomics.annotation.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.cchmc.bmi.snpomics.GenomicSpan;
import org.cchmc.bmi.snpomics.annotation.TranscriptAnnotation;
import org.cchmc.bmi.snpomics.util.StringUtils;

/**
 * Loads UCSC table-style gene descriptions into a JDBC backend.  The UCSC style varies from
 * table to table, so here are the columns expected by this class:
 * <ol>
 *  <li>Transcript ID</li>
 *  <li>Chromosome</li>
 *  <li>Strand (encoded as +/-)</li>
 *  <li>Transcription Start (0-based)</li>
 *  <li>Transcription End (1-based)</li>
 *  <li>CDS Start (0-based)</li>
 *  <li>CDS End (1-based).  If there is no CDS, CDS Start = CDS End = Transcription Start</li>
 *  <li>Exon Starts (comma-separated, 0-based)</li>
 *  <li>Exon Ends (comma-separated, 1-based)</li>
 *  <li>Protein ID</li>
 *  <li>Gene Symbol</li>
 *  <li>Any further columns are ignored</li>
 * </ol>
 * <p>At the time of this writing, the knownGenes table is very close to this format - it contains two
 * extra columns (exonCount between cdsEnd and exonStarts, and alignID at the end), and does not have
 * a gene symbol in it</p>
 * <p>Lines beginning with a # sign are ignored, so you can get tables by using the "selected fields
 * from primary and related tables" export function of the UCSC table browser</p>
 * @author dexzb9
 *
 */
public class TranscriptImporter extends JdbcImporter<TranscriptAnnotation> {

	@Override
	public boolean importAnnotations(InputStream input) {
		createTable();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		PreparedStatement stat = null;
		String line;
		try {
			stat = connection.prepareStatement(tableInsertStmt());
			while ((line = reader.readLine()) != null) {
				if (line.charAt(0) == '#')
					continue;
				String[] F = line.split("\t");
				GenomicSpan span = new GenomicSpan(F[1], Long.parseLong(F[3])+1, Long.parseLong(F[4]));
				List<String> exonStarts = new ArrayList<String>();
				for (String val : F[7].split(",")) {
					exonStarts.add(new Long(Long.valueOf(val)+1).toString());
				}
				stat.setInt(1, span.getBin());
				stat.setString(2, F[0]);
				stat.setString(3, F[10]);
				stat.setString(4, F[9]);
				stat.setString(5, span.getChromosome());
				stat.setString(6, F[2]);
				stat.setLong(7, span.getStart());
				stat.setLong(8, span.getEnd());
				stat.setLong(9, Long.parseLong(F[5])+1);
				stat.setLong(10, Long.parseLong(F[6]));
				stat.setString(11, StringUtils.join(",", exonStarts));
				stat.setString(12, F[8].substring(0, F[8].length()-1));
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

	@Override
	protected String tableCreationStmt() {
		return "CREATE TABLE `"+tableName+"` ("+
			"bin smallint(5) UNSIGNED NOT NULL, "+
			"id varchar(30) NOT NULL, "+
			"gene varchar(50) NOT NULL, "+
			"protein varchar(30) NOT NULL, "+
			"chrom varchar(30) NOT NULL, "+
			"strand char(1) NOT NULL, "+
			"txStart int(10) UNSIGNED NOT NULL, "+
			"txEnd int(10) UNSIGNED NOT NULL, "+
			"cdsStart int(10) UNSIGNED NOT NULL, "+
			"cdsEnd int(10) UNSIGNED NOT NULL, "+
			"exonStarts longblob NOT NULL, "+
			"exonEnds longblob NOT NULL, "+
			"PRIMARY KEY (id), "+
			"INDEX bin (chrom, bin))";
	}
	
	private String tableInsertStmt() {
		return "INSERT INTO `"+tableName+"` SET "+
			"bin=?, id=?, gene=?, protein=?, chrom=?, strand=?, txStart=?, txEnd=?, cdsStart=?, "+
			"cdsEnd=?, exonStarts=?, exonEnds=?";
	}
	
}
