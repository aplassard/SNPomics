package org.cchmc.bmi.snpomics.annotation.annotator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.cchmc.bmi.snpomics.annotation.reference.TranscriptAnnotation;
import org.cchmc.bmi.snpomics.annotation.reference.TranscriptAnnotationTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TranscriptEffectAnnotatorTest {
	
	private static TranscriptAnnotation fwdCoding = null;
	private static TranscriptAnnotation fwdNC = null;
	private static TranscriptAnnotation revCoding = null;
	private static TranscriptAnnotation revNC = null;
	private static TranscriptEffectAnnotator annotator = null;
	
	@BeforeClass
	public static void createTranscripts() {
		annotator = new TranscriptEffectAnnotator();
		fwdCoding = TranscriptAnnotationTest.getForwardCoding();
		fwdNC = TranscriptAnnotationTest.getForwardNoncoding();
		revCoding = TranscriptAnnotationTest.getReverseCoding();
		revNC = TranscriptAnnotationTest.getReverseNoncoding();
	}
	
	@AfterClass
	public static void cleanup() {
		annotator = null;
		fwdCoding = null;
		fwdNC = null;
		revCoding = null;
		revNC = null;
	}

	@Test
	public void midIntron() {
		assertThat(annotator.getHgvsCoord(fwdCoding, 10025), is("6+8"));
		assertThat(annotator.getHgvsCoord(fwdNC, 10025), is("17+8"));
		assertThat(annotator.getHgvsCoord(revCoding, 20055), is("7+17"));
		assertThat(annotator.getHgvsCoord(revNC, 20055), is("12+17"));
	}
	
	@Test
	public void firstIn5UTR() {
		assertThat(annotator.getHgvsCoord(fwdCoding, 10011), is("-1"));
		assertThat(annotator.getHgvsCoord(fwdNC, 10011), is("11"));
		assertThat(annotator.getHgvsCoord(revCoding, 20079), is("-1"));
		assertThat(annotator.getHgvsCoord(revNC, 20079), is("5"));
	}
	
	@Test
	public void firstIn3UTR() {
		assertThat(annotator.getHgvsCoord(fwdCoding, 10090), is("*1"));
		assertThat(annotator.getHgvsCoord(fwdNC, 10090), is("36"));
		assertThat(annotator.getHgvsCoord(revCoding, 20021), is("*1"));
		assertThat(annotator.getHgvsCoord(revNC, 20021), is("30"));
	}
	
	@Test
	public void firstInCDS() {
		assertThat(annotator.getHgvsCoord(fwdCoding, 10012), is("1"));
		assertThat(annotator.getHgvsCoord(fwdNC, 10012), is("12"));
		assertThat(annotator.getHgvsCoord(revCoding, 20078), is("1"));
		assertThat(annotator.getHgvsCoord(revNC, 20078), is("6"));
	}
	
	@Test
	public void lastInCDS() {
		assertThat(annotator.getHgvsCoord(fwdCoding, 10089), is("24"));
		assertThat(annotator.getHgvsCoord(fwdNC, 10089), is("35"));
		assertThat(annotator.getHgvsCoord(revCoding, 20022), is("24"));
		assertThat(annotator.getHgvsCoord(revNC, 20022), is("29"));
	}
	
	@Test
	public void firstInIntron() {
		assertThat(annotator.getHgvsCoord(fwdCoding, 10043), is("16+1"));
		assertThat(annotator.getHgvsCoord(fwdNC, 10043), is("27+1"));
		assertThat(annotator.getHgvsCoord(revCoding, 20071), is("7+1"));
		assertThat(annotator.getHgvsCoord(revNC, 20071), is("12+1"));
	}

	@Test
	public void lastInIntron() {
		assertThat(annotator.getHgvsCoord(fwdCoding, 10081), is("17-1"));
		assertThat(annotator.getHgvsCoord(fwdNC, 10081), is("28-1"));
		assertThat(annotator.getHgvsCoord(revCoding, 20039), is("8-1"));
		assertThat(annotator.getHgvsCoord(revNC, 20039), is("13-1"));
	}
	
	@Test
	public void upstream() {
		assertThat(annotator.getHgvsCoord(fwdCoding, 10000), is("-11-u1"));
		assertThat(annotator.getHgvsCoord(fwdNC, 10000), is("1-u1"));
		assertThat(annotator.getHgvsCoord(revCoding, 20084), is("-5-u1"));
		assertThat(annotator.getHgvsCoord(revNC, 20084), is("1-u1"));
	}
	
	@Test
	public void downstream() {
		assertThat(annotator.getHgvsCoord(fwdCoding, 10100), is("*10+d1"));
		assertThat(annotator.getHgvsCoord(fwdNC, 10100), is("45+d1"));
		assertThat(annotator.getHgvsCoord(revCoding, 20000), is("*21+d1"));
		assertThat(annotator.getHgvsCoord(revNC, 20000), is("50+d1"));
	}

}
