package org.cchmc.bmi.snpomics.annotation;


public class DummyAnnotation implements InteractiveAnnotation {

	@Abbreviation("DUM")
	@ShortName("Dummy")
	@Description("A simple 'annotation' that just describes the variation")
	public String getValue() {
		return value;
	}
	
	public void setValue(String newVal) {
		value = newVal;
	}
	
	private String value;

}
