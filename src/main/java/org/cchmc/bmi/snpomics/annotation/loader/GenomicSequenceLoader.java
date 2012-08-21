package org.cchmc.bmi.snpomics.annotation.loader;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.cchmc.bmi.snpomics.Genome;
import org.cchmc.bmi.snpomics.GenomicSpan;
import org.cchmc.bmi.snpomics.annotation.reference.GenomicSequenceAnnotation;
import org.cchmc.bmi.snpomics.util.FastaReader;

public class GenomicSequenceLoader extends
		JdbcLoader<GenomicSequenceAnnotation> implements
		MappedAnnotationLoader<GenomicSequenceAnnotation> {

	public GenomicSequenceLoader() {
		isInitialized = false;
	}
	
	private void initialize() {
		PreparedStatement stat = null;
		ResultSet rs = null;
		try {
			stat = connection.prepareStatement("SELECT * FROM `"+tableName+"`");
			rs = stat.executeQuery();
			if (rs.next()) {
				File fasta = new File(Genome.getReferenceDirectory(), rs.getString(1));
				reader = new FastaReader(fasta);
			}
			isInitialized = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stat != null)
					stat.close();
			} catch (SQLException e) {}
		}
	}
	
	@Override
	public GenomicSequenceAnnotation loadByID(String id) {
		List<GenomicSequenceAnnotation> result = loadByName(id);
		if (result != null && result.size() > 0)
			return result.get(0);
		return null;
	}

	@Override
	public List<GenomicSequenceAnnotation> loadByName(String name) {
		return loadByExactPosition(GenomicSpan.parseSpan(name));
	}

	@Override
	public List<GenomicSequenceAnnotation> loadByOverlappingPosition(
			GenomicSpan position) {
		return loadByExactPosition(position);
	}

	@Override
	public List<GenomicSequenceAnnotation> loadByExactPosition(GenomicSpan position) {
		if (!isInitialized)
			initialize();
		String seq = reader.getSequence(position);
		if (seq == null)
			return null;
		ArrayList<GenomicSequenceAnnotation> list = new ArrayList<GenomicSequenceAnnotation>(1);
		list.add(new GenomicSequenceAnnotation(position, seq));
		return list;
	}

	private boolean isInitialized;
	private FastaReader reader;
}
