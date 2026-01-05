package space.sunqian.verlink;

/**
 * Exception for verlink
 *
 * @author sunqian
 */
public class VerlinkException extends RuntimeException {

    /**
     * Constructs with empty message and cause.
     */
    public VerlinkException() {
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public VerlinkException(String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public VerlinkException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public VerlinkException(Throwable cause) {
        super(cause);
    }
}
