package org.cchmc.bmi.snpomics.annotation.reference;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.cchmc.bmi.snpomics.GenomicSpan;
import org.junit.BeforeClass;
import org.junit.Test;

public class TranscriptAnnotationTest {
	
	/*
	 * The forward test transcripts look like this:
         1         2         3         4         5         6         7         8         9         0
1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890
CCCCCCCCCCCATGGAA               TCATCCGCTG                                       GAGAGTAGAAAAAAAAAA
           123456               7890123456                                       7890123
 1                                 1               1         2         1            2             1
10987654321      123456787654321          123456789012345678909876543210987654321        12345678901
-----------      ++++++++-------          ++++++++++++++++++++-------------------        **********d

Top two rows are genomic coordinates (off by 10,000), followed by the transcribed sequence and the 
numbering of the CDS.  The next line is the tens unit for both the lines above and below it, followed
by the non-coding numbering.  The final line indicates the direction of counting.  Obviously, the CDS
only applies to the coding transcript...

Here are the reverse transcripts, in the same format.  Note that the sequence is already reverse
complemented and the genomic coordinates are descending:
   8         7         6         5         4         3         2         1
32109876543210987654321098765432109876543210987654321098765432109876543210987654321
CCCCCATGGCAG                                 GTATCTGTGCGCTATGAAAAAAAAAAAAAAAAAAAAAA
     1234567                                 89012345678901234
                     1             1           1         2             1         2
54321       123456789012345676543210987654321                 123456789012345678901
-----       +++++++++++++++++----------------                 *********************
	 */

	private static TranscriptAnnotation fwdCoding = null;
	private static TranscriptAnnotation fwdNC = null;
	private static TranscriptAnnotation revCoding = null;
	private static TranscriptAnnotation revNC = null;
	
	public static TranscriptAnnotation getForwardCoding() {
		if (fwdCoding == null) createTranscripts();
		return fwdCoding;
	}
	
	public static TranscriptAnnotation getForwardNoncoding() {
		if (fwdNC == null) createTranscripts();
		return fwdNC;
	}

	public static TranscriptAnnotation getReverseCoding() {
		if (revCoding == null) createTranscripts();
		return revCoding;
	}
	
	public static TranscriptAnnotation getReverseNoncoding() {
		if (revNC == null) createTranscripts();
		return revNC;
	}

	private static void createTranscripts() {
		fwdCoding = new TranscriptAnnotation();
		fwdCoding.setID("fC");
		fwdCoding.setName("fwdCoding");
		fwdCoding.setProtID("fC_prot");
		fwdCoding.setOnForwardStrand(true);
		fwdCoding.setPosition(new GenomicSpan("1", 10001, 10099));
		fwdCoding.setCds(new GenomicSpan("1", 10012, 10089));
		fwdCoding.setSplicedSequence("CCCCCCCCCCCATGGAATCATCCGCTGGAGAGTAGAAAAAAAAAA");
		fwdCoding.setExons(Arrays.asList(
				new GenomicSpan[] {
						GenomicSpan.parseSpan("1:10001-10017"),
						GenomicSpan.parseSpan("1:10033-10042"),
						GenomicSpan.parseSpan("1:10082-10099")}));
		
		fwdNC = new TranscriptAnnotation();
		fwdNC.setID("fNC");
		fwdNC.setName("fwdNonCoding");
		fwdNC.setOnForwardStrand(true);
		fwdNC.setPosition(new GenomicSpan("1", 10001, 10099));
		fwdNC.setCds(new GenomicSpan("1", 10001, 10000));
		fwdNC.setSplicedSequence("CCCCCCCCCCCATGGAATCATCCGCTGGAGAGTAGAAAAAAAAAA");
		fwdNC.setExons(Arrays.asList(
				new GenomicSpan[] {
						GenomicSpan.parseSpan("1:10001-10017"),
						GenomicSpan.parseSpan("1:10033-10042"),
						GenomicSpan.parseSpan("1:10082-10099")}));

		revCoding = new TranscriptAnnotation();
		revCoding.setID("rC");
		revCoding.setName("revCoding");
		revCoding.setProtID("rC_prot");
		revCoding.setOnForwardStrand(false);
		revCoding.setPosition(new GenomicSpan("2", 20001, 20083));
		revCoding.setCds(new GenomicSpan("2", 20022, 20078));
		revCoding.setSplicedSequence("CCCCCATGGCAGGTATCTGTGCGCTATGAAAAAAAAAAAAAAAAAAAAAA");
		revCoding.setExons(Arrays.asList(
				new GenomicSpan[] {
						GenomicSpan.parseSpan("2:20001-20038"),
						GenomicSpan.parseSpan("2:20072-20083")}));

		revNC = new TranscriptAnnotation();
		revNC.setID("rNC");
		revNC.setName("revNonCoding");
		revNC.setOnForwardStrand(false);
		revNC.setPosition(new GenomicSpan("2", 20001, 20083));
		revNC.setCds(new GenomicSpan("2", 20001, 20000));
		revNC.setSplicedSequence("CCCCCATGGCAGGTATCTGTGCGCTATGAAAAAAAAAAAAAAAAAAAAAA");
		revNC.setExons(Arrays.asList(
				new GenomicSpan[] {
						GenomicSpan.parseSpan("2:20001-20038"),
						GenomicSpan.parseSpan("2:20072-20083")}));
}
	
	@BeforeClass
	public static void setupTranscripts() {
		createTranscripts();
	}

	@Test
	public void checkProteinCoding() {
		assertThat(fwdCoding.isProteinCoding(), is(true));
		assertThat(fwdNC.isProteinCoding(), is(false));
		assertThat(revCoding.isProteinCoding(), is(true));
		assertThat(revNC.isProteinCoding(), is(false));
	}
	
	@Test
	public void check5utr() {
		assertThat(fwdCoding.get5UtrLength(), is(11));
		assertThat(fwdNC.get5UtrLength(), is(0));
		assertThat(revCoding.get5UtrLength(), is(5));
		assertThat(revNC.get5UtrLength(), is(0));
	}

	@Test
	public void check3utr() {
		assertThat(fwdCoding.get3UtrLength(), is(10));
		assertThat(fwdNC.get3UtrLength(), is(0));
		assertThat(revCoding.get3UtrLength(), is(21));
		assertThat(revNC.get3UtrLength(), is(0));
	}
	
	@Test
	public void checkCdsLength() {
		assertThat(fwdCoding.getCdsLength(), is(24));
		assertThat(fwdNC.getCdsLength(), is(45));
		assertThat(revCoding.getCdsLength(), is(24));
		assertThat(revNC.getCdsLength(), is(50));
	}
	
	@Test
	public void getCDS() {
		assertThat(fwdCoding.getCodingSequence(), is("ATGGAATCATCCGCTGGAGAGTAG"));
		assertThat(fwdNC.getCodingSequence(), is(""));
		assertThat(revCoding.getCodingSequence(), is("ATGGCAGGTATCTGTGCGCTATGA"));
		assertThat(revNC.getCodingSequence(), is(""));
	}
	

}
