package seedu.address.model.company.exceptions;

/**
 * Signals that a provided status string does not map to any supported application stage.
 */
public class UnsupportedStatusException extends RuntimeException {
    public UnsupportedStatusException(String status) {
        super("Unknown status: " + status);
    }
}

