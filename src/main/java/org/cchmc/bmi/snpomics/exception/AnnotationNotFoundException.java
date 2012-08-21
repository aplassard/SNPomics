package org.cchmc.bmi.snpomics.exception;

/**
 * Indicates that a requested reference annotation is not present
 * @author dexzb9
 *
 */
@SuppressWarnings("serial")
public class AnnotationNotFoundException extends SnpomicsException {

	public AnnotationNotFoundException() {
	}

	public AnnotationNotFoundException(String message) {
		super(message);
	}

	public AnnotationNotFoundException(Throwable cause) {
		super(cause);
	}

	public AnnotationNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
