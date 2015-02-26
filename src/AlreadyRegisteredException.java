/**
 * @author Conor Keegan
 *
 * This document is not meant for re-distribution
 */
public class AlreadyRegisteredException extends Exception {
	private static final long serialVersionUID = -4314717944319770564L;

	public AlreadyRegisteredException(String message) {
		super(message);
	}
}
