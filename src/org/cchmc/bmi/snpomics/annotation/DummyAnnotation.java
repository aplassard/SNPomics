package org.cchmc.bmi.snpomics.annotation;


@Abbreviation("DUM")
@ShortName("Dummy")
@Description("A simple 'annotation' that just describes the variation")
public class DummyAnnotation implements Annotation {

	@Override
	public String getName() {
		return value;
	}

	@Override
	public String getID() {
		return value;
	}
	
	@Override
	public String toString() {
		return value;
	}
	
	public void setValue(String newVal) {
		value = newVal;
	}
	
	private String value;

}
