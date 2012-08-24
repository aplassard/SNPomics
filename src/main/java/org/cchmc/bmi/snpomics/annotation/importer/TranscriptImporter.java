package org.cchmc.bmi.snpomics.annotation.importer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.cchmc.bmi.snpomics.GenomicSpan;
import org.cchmc.bmi.snpomics.annotation.reference.TranscriptAnnotation;
import org.cchmc.bmi.snpomics.exception.SnpomicsException;
import org.cchmc.bmi.snpomics.exception.UserException;
import org.cchmc.bmi.snpomics.util.BaseUtils;
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
 * <p>Any transcripts which have the gene symbol '<code>abParts</code>' will be <b>skipped</b> (ie, not 
 * imported).  This is an ugly, hackish, special case, but these are catchall antibody "genes" in the
 * UCSC model that can be enormous (the largest in humans, on chr14, has 4461 exons.  Compare that to 
 * titin, which has ~200)</p>
 * @author dexzb9
 *
 */
public class TranscriptImporter extends JdbcImporter<TranscriptAnnotation> {

	@Override
	public boolean importAnnotations(Reader input) {
		if (fasta == null)
			throw new UserException.FastaNotSet();
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
				if (F[10].equals("abParts"))
					continue;
				GenomicSpan span = new GenomicSpan(F[1], Long.parseLong(F[3])+1, Long.parseLong(F[4]));
				List<String> exonStarts = new ArrayList<String>();
				for (String val : F[7].split(",")) {
					exonStarts.add(new Long(Long.valueOf(val)+1).toString());
				}
				String[] prots = F[9].split(",");
				String protein="";
				if (prots != null)
					for (String p : prots)
						if (!p.isEmpty()) {
							protein = p;
							break;
						}
				stat.setInt(1, span.getBin());
				stat.setString(2, F[0]);
				stat.setString(3, F[10]);
				stat.setString(4, protein);
				stat.setString(5, span.getChromosome());
				stat.setString(6, F[2]);
				stat.setLong(7, span.getStart());
				stat.setLong(8, span.getEnd());
				stat.setLong(9, Long.parseLong(F[5])+1);
				stat.setLong(10, Long.parseLong(F[6]));
				stat.setString(11, StringUtils.join(",", exonStarts));
				stat.setString(12, F[8].substring(0, F[8].length()-1));
				stat.setBytes(13, compress(getSequence(span.getChromosome(), F[2], exonStarts, F[8])));
				try {
					stat.execute();
				} catch (SQLIntegrityConstraintViolationException e) {
					System.err.println("Skipping "+F[0]+"("+F[10]+"): "+e.getMessage());
				}
			}
		} catch (SQLException e) {
			throw new UserException.SQLError(e);
		} catch (IOException e) {
			throw new UserException.IOError(e);
		} finally {
			try {
				if (stat != null)
					stat.close();
			} catch (SQLException e) {}
		}
		return true;
	}
	
	private byte[] compress(String toCompress) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try {
			PrintWriter output = new PrintWriter(new GZIPOutputStream(bytes));
			output.print(toCompress);
			output.close();
		} catch (IOException e) {
			throw new SnpomicsException("Error compressing sequence", e);
		}
		return bytes.toByteArray();

	}
	
	private String getSequence(String chrom, String strand, List<String> starts, String endStr) {
		List<String> ends = Arrays.asList(endStr.split(","));
		//Since the UCSC string has a trailing comma, ends.size() == starts.size()+1
		StringBuilder sb = new StringBuilder();
		GenomicSpan exon = new GenomicSpan();
		exon.setChromosome(chrom);
		for (int i=0; i<starts.size(); i++) {
			exon.setStart(Long.parseLong(starts.get(i)));
			exon.setEnd(Long.parseLong(ends.get(i)));
			sb.append(fasta.getSequence(exon));
		}
		if (strand.equals("-"))
			return BaseUtils.reverseComplement(sb.toString());
		return sb.toString();
	}

	@Override
	protected String tableCreationStmt() {
		return "CREATE TABLE `"+tableName+"` ("+
			"bin smallint(5) NOT NULL, "+
			"id varchar(30) NOT NULL, "+
			"gene varchar(50) NOT NULL, "+
			"protein varchar(30) NOT NULL, "+
			"chrom varchar(30) NOT NULL, "+
			"strand char(1) NOT NULL, "+
			"txStart int(10) NOT NULL, "+
			"txEnd int(10) NOT NULL, "+
			"cdsStart int(10) NOT NULL, "+
			"cdsEnd int(10) NOT NULL, "+
			"exonStarts varbinary(5000) NOT NULL, "+
			"exonEnds varbinary(5000) NOT NULL, "+
			"txSequence blob NOT NULL, "+
			"PRIMARY KEY (id))";
	}
		
	@Override
	protected String indexCreationStmt() {
		return "CREATE INDEX bin ON `"+tableName+"` (chrom, bin)";
	}

	private String tableInsertStmt() {
		return "INSERT INTO `"+tableName+"` " +
				"(bin, id, gene, protein, chrom, strand, txStart, txEnd, cdsStart, cdsEnd, " +
				"exonStarts, exonEnds, txSequence) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	}
	
}
