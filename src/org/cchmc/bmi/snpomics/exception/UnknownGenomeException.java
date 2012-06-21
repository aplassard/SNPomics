package org.cchmc.bmi.snpomics.exception;

@SuppressWarnings("serial")
public class UnknownGenomeException extends UncheckedSnpomicsException {

	public UnknownGenomeException() {
	}

	public UnknownGenomeException(String message) {
		super(message);
	}

	public UnknownGenomeException(Throwable cause) {
		super(cause);
	}

	public UnknownGenomeException(String message, Throwable cause) {
		super(message, cause);
	}

}
