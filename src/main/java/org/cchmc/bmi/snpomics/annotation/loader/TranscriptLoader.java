package org.cchmc.bmi.snpomics.annotation.loader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.cchmc.bmi.snpomics.GenomicSpan;
import org.cchmc.bmi.snpomics.annotation.reference.TranscriptAnnotation;
import org.cchmc.bmi.snpomics.exception.SnpomicsException;
import org.cchmc.bmi.snpomics.exception.UserException;

public class TranscriptLoader extends JdbcLoader<TranscriptAnnotation> 
		implements MappedAnnotationLoader<TranscriptAnnotation> {

	public TranscriptLoader() {
		cache = new ArrayList<TranscriptAnnotation>();
		cacheRegion = new GenomicSpan();
		isLookaheadEnabled = true;
	}
	
	public void enableLookaheadCache() {
		isLookaheadEnabled = true;
	}
	
	public void disableLookaheadCache() {
		isLookaheadEnabled = false;
	}
	
	@Override
	public TranscriptAnnotation loadByID(String id) {
		PreparedStatement stat = null;
		ResultSet rs = null;
		try {
			stat = connection.prepareStatement("SELECT "+columnsToSelect+" FROM `"+tableName+"` WHERE id=?");
			stat.setString(1, id);
			rs = stat.executeQuery();
			if (rs.next())
				return createTranscriptFromRS(rs);
		} catch (SQLException e) {
			throw new UserException.SQLError(e);
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
			stat = connection.prepareStatement("SELECT "+columnsToSelect+" FROM `"+tableName+"` WHERE gene=?");
			stat.setString(1, name);
			rs = stat.executeQuery();
			while (rs.next())
				result.add(createTranscriptFromRS(rs));
		} catch (SQLException e) {
			throw new UserException.SQLError(e);
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
		List<TranscriptAnnotation> result;
		if (isLookaheadEnabled) {
			if (!cacheRegion.contains(position)) {
				cacheRegion = new GenomicSpan(position.getChromosome(), position.getStart(), position.getEnd()+1000);
				cache = loadRegion(cacheRegion);
			}
			result = new ArrayList<TranscriptAnnotation>();
			for (TranscriptAnnotation tx : cache) {
				if (tx.overlaps(position))
					result.add(tx);
			}
		} else
			result = loadRegion(position);
		return result;
	}
	
	private List<TranscriptAnnotation> loadRegion(GenomicSpan position) {
		List<TranscriptAnnotation> result = new ArrayList<TranscriptAnnotation>();
		List<Integer> bins = position.getOverlappingBins();
		PreparedStatement stat = null;
		ResultSet rs = null;
		try {
			stat = connection.prepareStatement("SELECT "+columnsToSelect+" FROM `"+tableName+
					"` WHERE chrom=? AND bin=? AND txEnd>=? AND txStart<=?");
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
			throw new UserException.SQLError(e);
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
			stat = connection.prepareStatement("SELECT "+columnsToSelect+" FROM `"+tableName+
					"` WHERE chrom=? AND bin=? AND txEnd=? AND txStart=?");
			stat.setString(1, position.getChromosome());
			stat.setInt(2, bin);
			stat.setLong(3, position.getStart());
			stat.setLong(4, position.getEnd());
			rs = stat.executeQuery();
			while (rs.next())
				result.add(createTranscriptFromRS(rs));
		} catch (SQLException e) {
			throw new UserException.SQLError(e);
		} finally {
			try {
				if (stat != null)
					stat.close();
			} catch (SQLException e) {}
		}
		return result;
	}
	
	public boolean loadSequence(TranscriptAnnotation tx) {
		if (!tx.getTranscribedSequence().isEmpty())
			return true;
		PreparedStatement stat = null;
		ResultSet rs = null;
		try {
			stat = connection.prepareStatement("SELECT txSequence FROM `"+tableName+"` WHERE id=?");
			stat.setString(1, tx.getID());
			rs = stat.executeQuery();
			if (rs.next()) {
				BufferedReader reader = new BufferedReader(
										new InputStreamReader(
										new GZIPInputStream(
										new ByteArrayInputStream(rs.getBytes(1)))));
				tx.setTranscribedSequence(reader.readLine());
				reader.close();

				return true;
			}
		} catch (IOException e) {
			throw new SnpomicsException("Error decompressing sequence", e);
		} catch (SQLException e) {
			throw new UserException.SQLError(e);
		} finally {
			try {
				if (stat != null)
					stat.close();
			} catch (SQLException e) {}
		}
		return false;
	}
	
	private TranscriptAnnotation createTranscriptFromRS(ResultSet rs) {
		TranscriptAnnotation tx = new TranscriptAnnotation();
		try {
			tx.setID(rs.getString("id"));
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
			throw new UserException.SQLError(e);
		}
		return tx;
	}

	private GenomicSpan cacheRegion;
	private List<TranscriptAnnotation> cache;
	private boolean isLookaheadEnabled;
	private String columnsToSelect = 
		"id, gene, protein, chrom, strand, txStart, txEnd, cdsStart, cdsEnd, exonStarts, exonEnds";
}
