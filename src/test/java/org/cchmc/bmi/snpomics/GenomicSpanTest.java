package org.cchmc.bmi.snpomics;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class GenomicSpanTest {
	
	//TODO: Test some of the bin calculations

	@Test
	public void parseTest() {
		GenomicSpan a = GenomicSpan.parseSpan("1:1-100");
		assertThat(a.getChromosome(), is("1"));
		assertThat(a.getStart(), is(1L));
		assertThat(a.getEnd(), is(100L));
		a = GenomicSpan.parseSpan("blah:45");
		assertThat(a.getChromosome(), is("blah"));
		assertThat(a.getStart(), is(45L));
		assertThat(a.getEnd(), is(45L));
	}
	
	@Test
	public void constructorTest() {
		GenomicSpan a = new GenomicSpan("alpha", 123456, 123789);
		assertThat(a.getChromosome(), is("alpha"));
		assertThat(a.getStart(), is(123456L));
		assertThat(a.getEnd(), is(123789L));
	}
	
	@Test
	public void overlapTest() {
		assertThat(GenomicSpan.parseSpan("A:1-10").overlaps(GenomicSpan.parseSpan("A:10-20")), is(true));
		assertThat(GenomicSpan.parseSpan("A:10-20").overlaps(GenomicSpan.parseSpan("A:1-10")), is(true));
		assertThat(GenomicSpan.parseSpan("A:1-10").overlaps(GenomicSpan.parseSpan("A:11-20")), is(false));
		assertThat(GenomicSpan.parseSpan("A:11-20").overlaps(GenomicSpan.parseSpan("A:1-10")), is(false));
		assertThat(GenomicSpan.parseSpan("A:1-10").overlaps(GenomicSpan.parseSpan("B:1-25")), is(false));
		assertThat(GenomicSpan.parseSpan("A:1-10").overlaps(GenomicSpan.parseSpan("A:4-7")), is(true));
		assertThat(GenomicSpan.parseSpan("A:4-7").overlaps(GenomicSpan.parseSpan("A:1-10")), is(true));
		assertThat(GenomicSpan.parseSpan("A:1-10").overlaps(GenomicSpan.parseSpan("A:10")), is(true));
		assertThat(GenomicSpan.parseSpan("A:1-10").overlaps(GenomicSpan.parseSpan("A:11")), is(false));
		assertThat(GenomicSpan.parseSpan("A:10-20").overlaps(GenomicSpan.parseSpan("A:10")), is(true));
		assertThat(GenomicSpan.parseSpan("A:10-20").overlaps(GenomicSpan.parseSpan("A:9")), is(false));
	}

	
	@Test
	public void containTest() {
		assertThat(GenomicSpan.parseSpan("A:1-10").contains(GenomicSpan.parseSpan("A:10-20")), is(false));
		assertThat(GenomicSpan.parseSpan("A:10-20").contains(GenomicSpan.parseSpan("A:1-10")), is(false));
		assertThat(GenomicSpan.parseSpan("A:1-10").contains(GenomicSpan.parseSpan("A:11-20")), is(false));
		assertThat(GenomicSpan.parseSpan("A:11-20").contains(GenomicSpan.parseSpan("A:1-10")), is(false));
		assertThat(GenomicSpan.parseSpan("A:1-10").contains(GenomicSpan.parseSpan("B:1-25")), is(false));
		assertThat(GenomicSpan.parseSpan("A:1-10").contains(GenomicSpan.parseSpan("A:4-7")), is(true));
		assertThat(GenomicSpan.parseSpan("A:4-7").contains(GenomicSpan.parseSpan("A:1-10")), is(false));
	}
}
