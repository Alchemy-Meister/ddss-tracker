package tracker.exceptions;

/** Base class for all the tracker's exceptions
 * @author Irene
 * @author Jesus
 */
public abstract class TrackerException extends Exception {

	private static final long serialVersionUID = -7464404799997633960L;

	
	public TrackerException(String message) {
		super(message);
	}
}
