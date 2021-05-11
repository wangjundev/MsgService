package com.stv.msgservice.core.mmslib;

/**
 * Thrown when an invalid header value was set.
 */
public class InvalidHeaderValueException extends com.stv.msgservice.core.mmslib.MmsException {
    private static final long serialVersionUID = -2053384496042052262L;

    /**
     * Constructs an InvalidHeaderValueException with no detailed message.
     */
    public InvalidHeaderValueException() {
        super();
    }

    /**
     * Constructs an InvalidHeaderValueException with the specified detailed message.
     *
     * @param message the detailed message.
     */
    public InvalidHeaderValueException(String message) {
        super(message);
    }
}
