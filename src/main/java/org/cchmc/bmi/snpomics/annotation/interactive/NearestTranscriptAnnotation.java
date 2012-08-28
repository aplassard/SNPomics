package org.cchmc.bmi.snpomics.annotation.interactive;

import java.util.List;

import org.cchmc.bmi.snpomics.annotation.reference.TranscriptAnnotation;

public class NearestTranscriptAnnotation implements InteractiveAnnotation {

	@MetaAnnotation(name="NearestTx", description="The closest transcript to this position",
			ref=TranscriptAnnotation.class)
	public String closestTranscript() {
		return getTxNum(0);
	}

	@MetaAnnotation(name="SecondNearestTx", description="The second closest transcript to this position",
			ref=TranscriptAnnotation.class)
	public String closestTranscript2() {
		return getTxNum(1);
	}
	
	@MetaAnnotation(name="NearestTxDist", description="The distance to the closest transcript to this position",
			ref=TranscriptAnnotation.class)
	public String closestTranscriptDistance() {
		return getTxDistNum(0);
	}

	@MetaAnnotation(name="SecondNearestTxDist", description="The distance to the second closest transcript to this position",
			ref=TranscriptAnnotation.class)
	public String closestTranscript2Distance() {
		return getTxDistNum(1);
	}

	private String getTxNum(int num) {
		if (nearestTx == null || nearestTx.size() <= num)
			return "[None]";
		return nearestTx.get(num).getID();
	}

	private String getTxDistNum(int num) {
		if (txDistances == null || txDistances.size() <= num)
			return "";
		return Long.toString(txDistances.get(num));
	}

	@MetaAnnotation(name="NearestGene", description="The closest gene to this position",
			ref=TranscriptAnnotation.class)
	public String closestGene() {
		return getGeneNum(0);
	}

	@MetaAnnotation(name="SecondNearestGene", description="The second closest gene to this position",
			ref=TranscriptAnnotation.class)
	public String closestGene2() {
		return getGeneNum(1);
	}
	
	@MetaAnnotation(name="NearestGeneDist", description="The distance to the closest gene to this position",
			ref=TranscriptAnnotation.class)
	public String closestGeneDistance() {
		return getGeneDistNum(0);
	}

	@MetaAnnotation(name="SecondNearestGeneDist", description="The distance to the second closest gene to this position",
			ref=TranscriptAnnotation.class)
	public String closestGene2Distance() {
		return getGeneDistNum(1);
	}

	private String getGeneNum(int num) {
		if (nearestGene == null || nearestGene.size() <= num)
			return "[None]";
		return nearestGene.get(num).getName();
	}

	private String getGeneDistNum(int num) {
		if (geneDistances == null || geneDistances.size() <= num)
			return "";
		return Long.toString(geneDistances.get(num));
	}

	public void setNearestTranscripts(List<TranscriptAnnotation> tx, List<Long> dist) {
		nearestTx = tx;
		txDistances = dist;
	}
	
	public void setNearestGenes(List<TranscriptAnnotation> tx, List<Long> dist) {
		nearestGene = tx;
		geneDistances = dist;
	}
	
	public List<TranscriptAnnotation> getNearestTx() {
		return nearestTx;
	}

	public List<Long> getTxDistances() {
		return txDistances;
	}

	public List<TranscriptAnnotation> getNearestGene() {
		return nearestGene;
	}

	public List<Long> getGeneDistances() {
		return geneDistances;
	}

	private List<TranscriptAnnotation> nearestTx;
	private List<Long> txDistances;
	private List<TranscriptAnnotation> nearestGene;
	private List<Long> geneDistances;
}
