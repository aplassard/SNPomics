package org.cchmc.bmi.snpomics;

import java.net.URI;
import java.util.Date;

import org.cchmc.bmi.snpomics.annotation.ReferenceAnnotation;

public class ReferenceMetadata<T extends ReferenceAnnotation> {

	public ReferenceMetadata(Class<T> annotationCls, String genome, String version) {
		cls = annotationCls;
		this.genome = genome;
		this.version = version;
	}
	
	public Class<T> getAnnotationClass() {
		return cls;
	}
	public Date getUpdateDate() {
		return update_date;
	}
	public void setUpdateDate(Date updateDate) {
		this.update_date = updateDate;
	}
	public URI getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = URI.create(source);
	}
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	public String getGenome() {
		return genome;
	}
	public String getVersion() {
		return version;
	}
	/**
	 * Sets the template for creating deep links to the annotations
	 * @param template a well-formed link.  All copies of the exact substring {?} will be 
	 * replaced by the ID of the annotation
	 */
	public void setLinkTemplate(String template) {
		deep_link = template;
	}
	public String getLinkTemplate() {
		return deep_link;
	}
	public URI getLink(T value) {
		String link = deep_link.replaceAll("\\{\\?\\}", value.getID());
		return URI.create(link);
	}

	@Override
	final public boolean equals(Object otherObject) {
		if (this == otherObject) return true;
		if (otherObject == null) return false;
		if (!(otherObject instanceof ReferenceMetadata)) return false;
		ReferenceMetadata<?> other = (ReferenceMetadata<?>)otherObject;
		return genome.equals(other.genome) && cls.equals(other.cls) && version.equals(other.version);
	}
	
	@Override
	final public int hashCode() {
		return 79*genome.hashCode() +
				43*cls.hashCode() +
				version.hashCode();
	}
	
	@Override
	public String toString() {
		return getClass().getName()+"[genome="+genome+", cls="+cls.getCanonicalName()+
		", version="+version+"]";
	}

	private final String genome;
	private final Class<T> cls;
	private final String version;
	private Date update_date;
	private String deep_link;
	private URI source;
	private boolean isDefault;
}
