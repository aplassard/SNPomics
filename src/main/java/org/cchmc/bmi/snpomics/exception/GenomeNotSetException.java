package org.cchmc.bmi.snpomics.exception;

@SuppressWarnings("serial")
public class GenomeNotSetException extends UncheckedSnpomicsException {

	public GenomeNotSetException() {
	}

	public GenomeNotSetException(String message) {
		super(message);
	}

	public GenomeNotSetException(Throwable cause) {
		super(cause);
	}

	public GenomeNotSetException(String message, Throwable cause) {
		super(message, cause);
	}

}
