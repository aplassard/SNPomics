package org.cchmc.bmi.snpomics.exception;

/**
 * The base class of all exceptions created specifically for SNPomics
 * @author dexzb9
 *
 */
@SuppressWarnings("serial")
public class SnpomicsException extends RuntimeException {

	public SnpomicsException() {
	}

	public SnpomicsException(String message) {
		super(message);
	}

	public SnpomicsException(Throwable cause) {
		super(cause);
	}

	public SnpomicsException(String message, Throwable cause) {
		super(message, cause);
	}

}
