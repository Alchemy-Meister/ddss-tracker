package tracker.exceptions;

/**
 * Exception thrown at the packet parser.
 * @author Irene
 * @author Jesus
 */
public class PacketParserException extends TrackerException {

	private static final long serialVersionUID = -2227594562203846115L;

	public PacketParserException(String message) {
		super(message);
	}
}
