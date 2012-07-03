package org.cchmc.bmi.snpomics.annotation.loader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.cchmc.bmi.snpomics.GenomicSpan;
import org.cchmc.bmi.snpomics.annotation.reference.TranscriptAnnotation;

public class TranscriptLoader extends JdbcLoader<TranscriptAnnotation> 
		implements MappedAnnotationLoader<TranscriptAnnotation> {

	@Override
	public TranscriptAnnotation loadByID(String id) {
		PreparedStatement stat = null;
		ResultSet rs = null;
		try {
			stat = connection.prepareStatement("SELECT * FROM `"+tableName+"` WHERE id=?");
			stat.setString(1, id);
			rs = stat.executeQuery();
			if (rs.next())
				return createTranscriptFromRS(rs);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (stat != null)
					stat.close();
			} catch (SQLException e) {}
		}
		return null;
	}

	@Override
	public List<TranscriptAnnotation> loadByName(String name) {
		List<TranscriptAnnotation> result = new ArrayList<TranscriptAnnotation>();
		PreparedStatement stat = null;
		ResultSet rs = null;
		try {
			stat = connection.prepareStatement("SELECT * FROM `"+tableName+"` WHERE gene=?");
			stat.setString(1, name);
			rs = stat.executeQuery();
			while (rs.next())
				result.add(createTranscriptFromRS(rs));
		} catch (SQLException e) {
			e.printStackTrace();
			return result;
		} finally {
			try {
				if (stat != null)
					stat.close();
			} catch (SQLException e) {}
		}
		return result;
	}

	@Override
	public List<TranscriptAnnotation> loadByOverlappingPosition(GenomicSpan position) {
		List<TranscriptAnnotation> result = new ArrayList<TranscriptAnnotation>();
		List<Integer> bins = position.getOverlappingBins();
		PreparedStatement stat = null;
		ResultSet rs = null;
		try {
			stat = connection.prepareStatement("SELECT * FROM `"+tableName+"` WHERE chrom=?"+
					" AND bin=? AND txEnd>=? AND txStart<=?");
			stat.setString(1, position.getChromosome());
			stat.setLong(3, position.getStart());
			stat.setLong(4, position.getEnd());
			for (Integer bin : bins) {
				stat.setInt(2, bin);
				rs = stat.executeQuery();
				while (rs.next())
					result.add(createTranscriptFromRS(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return result;
		} finally {
			try {
				if (stat != null)
					stat.close();
			} catch (SQLException e) {}
		}
		return result;
	}

	@Override
	public List<TranscriptAnnotation> loadByExactPosition(GenomicSpan position) {
		List<TranscriptAnnotation> result = new ArrayList<TranscriptAnnotation>();
		int bin = position.getBin();
		PreparedStatement stat = null;
		ResultSet rs = null;
		try {
			stat = connection.prepareStatement("SELECT * FROM `"+tableName+"` WHERE chrom=?"+
					" AND bin=? AND txEnd=? AND txStart=?");
			stat.setString(1, position.getChromosome());
			stat.setInt(2, bin);
			stat.setLong(3, position.getStart());
			stat.setLong(4, position.getEnd());
			rs = stat.executeQuery();
			while (rs.next())
				result.add(createTranscriptFromRS(rs));
		} catch (SQLException e) {
			e.printStackTrace();
			return result;
		} finally {
			try {
				if (stat != null)
					stat.close();
			} catch (SQLException e) {}
		}
		return result;
	}
	
	private TranscriptAnnotation createTranscriptFromRS(ResultSet rs) {
		TranscriptAnnotation tx = new TranscriptAnnotation();
		try {
			tx.setId(rs.getString("id"));
			tx.setName(rs.getString("gene"));
			tx.setProtID(rs.getString("protein"));
			GenomicSpan pos = new GenomicSpan(rs.getString("chrom"), rs.getLong("txStart"), 
					rs.getLong("txEnd"));
			tx.setPosition(pos);
			pos.setStart(rs.getLong("cdsStart"));
			pos.setEnd(rs.getLong("cdsEnd"));
			tx.setCds(pos);
			tx.setOnForwardStrand(rs.getString("strand").equals("+"));
			List<GenomicSpan> exons = new ArrayList<GenomicSpan>();
			String[] starts = rs.getString("exonStarts").split(",");
			String[] ends = rs.getString("exonEnds").split(",");
			for (int i=0;i<starts.length;i++) {
				GenomicSpan g = new GenomicSpan(pos.getChromosome(), Long.parseLong(starts[i]), 
						Long.parseLong(ends[i]));
				exons.add(g);
			}
			tx.setExons(exons);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return tx;
	}

}
