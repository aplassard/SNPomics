package org.cchmc.bmi.snpomics.exception;

@SuppressWarnings("serial")
public class UncheckedSnpomicsException extends RuntimeException {

	public UncheckedSnpomicsException() {
	}

	public UncheckedSnpomicsException(String message) {
		super(message);
	}

	public UncheckedSnpomicsException(Throwable cause) {
		super(cause);
	}

	public UncheckedSnpomicsException(String message, Throwable cause) {
		super(message, cause);
	}

}
