package org.cchmc.bmi.snpomics.exception;

/**
 * Blatantly copied from the GATK...  UserExceptions are problems caused by something the
 * user did wrong/could correct
 * @author dexzb9
 *
 */
@SuppressWarnings("serial")
public class UserException extends SnpomicsException {
	public UserException(String message) { super(message); }
	public UserException(String message, Throwable cause) { super(message, cause); }
	@SuppressWarnings("unused")
	private UserException(Throwable caught) { super(caught); }
	
	public static class GenomeNotSet extends UserException {
		public GenomeNotSet() {
			super("Genome must be specified with -g");
		}
	}

	public static class FastaNotSet extends UserException {
		public FastaNotSet() {
			super("Reference sequence is required but not set, specify with -f");
		}
	}

	public static class OutOfMemory extends UserException {
		public OutOfMemory() {
			super("Insufficient memory given to virtual machine.  Rerun with java -Xmx");
		}
	}
	
	public static class UnknownGenome extends UserException {
		public UnknownGenome(String unknownGenome) {
			super(String.format("'%s' is not a recognized genome.  Run with 'list genomes' to see known genomes", unknownGenome));
		}
	}
	
	public static class AnnotationNotFound extends UserException {
		public AnnotationNotFound(String annotationType) {
			super(String.format("Reference annotation type '%s' is needed but doesn't exist", annotationType));
		}
	}
	
	public static class SQLError extends UserException {
		public SQLError(Throwable cause) {
			super("Error interacting with SQL database", cause);
		}
	}

	public static class IOError extends UserException {
		public IOError(Throwable cause) {
			super("I/O error", cause);
		}
	}

	public static class FileNotFound extends UserException {
		public FileNotFound(Throwable cause) {
			super("File not found!", cause);
		}
	}
	
	public static class UnknownTranslationTable extends UserException {
		public UnknownTranslationTable(int badID) {
			super("Unknown translation table "+Integer.toString(badID));
		}
	}
}
